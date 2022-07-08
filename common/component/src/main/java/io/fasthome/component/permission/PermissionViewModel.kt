package io.fasthome.component.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import io.fasthome.component.R
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.mvi.MessageResult
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PermissionViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val applicationContext: Context,
) : BaseViewModel<PermissionState, PermissionEvent>(router, requestParams), PermissionInterface {

    private class Request(
        val permission: String,
        val canOpenSettings: Boolean,
        val listener: (isGranted: Boolean) -> Unit,
    )

    companion object {
        /**
         * [requests] хранятся в companion object, т.к. Activity имеет единую очередь запроса разрешений.
         */
        private val mutexRequests = Mutex()
        private var requests = emptySet<Request>()
    }

    private var appSettingNavigationDeferred: CompletableDeferred<Unit>? = null

    private val alertsDeferred = mutableMapOf<String, CompletableDeferred<MessageResult>>()

    override fun createInitialState() = PermissionState()

    override fun has(permission: String): Boolean {
        val checkResult = ContextCompat.checkSelfPermission(applicationContext, permission)
        return checkResult == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun request(permission: String, canOpenSettings: Boolean): Boolean {
        val result = CompletableDeferred<Boolean>()

        viewModelScope.launch {
            val requestNow = mutexRequests.withLock {
                val requestWereEmpty = requests.isEmpty()
                requests = requests + Request(
                    permission = permission,
                    canOpenSettings = canOpenSettings,
                    listener = result::complete,
                )
                requestWereEmpty
            }
            /**
             * Отправка PermissionEvent.RequestPermission выполняется вне mutexRequests.withLock,
             * чтобы избежать вложенной блокировки mutex.
             */
            if (requestNow) {
                sendEvent(PermissionEvent.RequestPermission(permission))
            }
        }

        return result.await()
    }

    fun onPermissionResult(permission: String, isGranted: Boolean, shouldShowRequestPermissionRationale: Boolean) {
        viewModelScope.launch {
            val permissionToRequest = mutexRequests.withLock {

                val (requestsForPermission, otherRequests) = requests.partition { it.permission == permission }
                requests = otherRequests.toSet()

                val result = if (isGranted) {
                    true
                } else {
                    if (!shouldShowRequestPermissionRationale && requestsForPermission.any { it.canOpenSettings }) {
                        val alert = Message.Alert(
                            id = "MESSAGE_ID_ASK_OPEN_SETTINGS",
                            titleText = PrintableText.StringResource(R.string.permission_settings_dialog_title),
                            messageText = PrintableText.StringResource(R.string.permission_settings_dialog_message),
                            actionText = PrintableText.StringResource(R.string.permission_settings_dialog_open_settings),
                        )
                        if (suspendShowAlert(alert).action == MessageResult.Action.Positive) {
                            suspendLaunchAppSettings()
                        }
                    }
                    has(permission)
                }
                requestsForPermission.forEach { it.listener(result) }

                requests.firstOrNull()?.permission
            }

            /**
             * Отправка PermissionEvent.RequestPermission выполняется вне mutexRequests.withLock,
             * чтобы избежать вложенной блокировки mutex.
             */
            if (permissionToRequest != null) {
                sendEvent(PermissionEvent.RequestPermission(permissionToRequest))
            }
        }
    }

    override fun onMessageResult(result: MessageResult) {
        if (result.id != Message.ID_NO_MATTER) {
            alertsDeferred.remove(result.id)?.complete(result)
        } else {
            super.onMessageResult(result)
        }
    }

    private suspend fun suspendShowAlert(alert: Message.Alert): MessageResult {
        require(alert.id != Message.ID_NO_MATTER)
        val completableDeferred = CompletableDeferred<MessageResult>()
        alertsDeferred[alert.id] = completableDeferred
        showMessage(alert)
        return completableDeferred.await()
    }

    private suspend fun suspendLaunchAppSettings() {
        val completableDeferred = CompletableDeferred<Unit>()
        appSettingNavigationDeferred = completableDeferred
        completableDeferred.await()
    }

}