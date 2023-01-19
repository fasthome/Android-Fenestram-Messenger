package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.core.exceptions.UnauthorizedException
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.coroutines.launch

class CodeViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val authInteractor: AuthInteractor,
    private val params: CodeNavigationContract.Params,
    private val permissionInterface: PermissionInterface,
) : BaseViewModel<CodeState, CodeEvent>(router, requestParams) {

    private val personalityLauncher = registerScreen(PersonalityNavigationContract) { result ->
        exitWithResult(CodeNavigationContract.createResult(result))
    }

    private val resendCodeTimer = ResendCodeTimer(duration.toLong(), step.toLong()) {
        sendEvent(CodeEvent.ChangeTime(this))
    }

    init {
        resendCodeTimer.start()
        viewModelScope.launch {
            permissionInterface.request(Manifest.permission.RECEIVE_SMS)
        }
    }

    override fun createInitialState(): CodeState {
        return CodeState(
            loading = null,
            error = null,
            autoFilling = null,
            phone = PrintableText.StringResource(R.string.auth_phone_sent, params.phoneNumber)
        )
    }

    fun checkCode(code: String) {
        updateState { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            when (val loginResult =
                authInteractor.login(params.phoneNumber, code)) {
                is CallResult.Error -> {
                    updateState { it.copy(loading = false) }
                    onError(loginResult.error, code)
                }
                is CallResult.Success -> {
                    updateState { it.copy(loading = false) }
                    onSuccess(loginResult.data)
                }
            }
        }
    }

    fun autoFillCode(code: String) {
        updateState { state->
            state.copy(error = null, loading = false, autoFilling = code) }
    }

    fun resendCode() {
        resendCodeTimer.start()
        viewModelScope.launch {
            /**
             * Отпрвка кода на телефон
             */
            authInteractor.sendCode(params.phoneNumber)
        }
    }

    private fun onSuccess(result: LoginResult) {
        personalityLauncher.launch(
            PersonalityNavigationContract.Params(
                auth = false,
                userDetail = result.userDetail
            )
        )
    }

    private fun onError(error: Throwable, code: String) {
        when (error) {
            is UnauthorizedException -> {
                updateState { state ->
                    state.copy(error = error)
                }
            }
        }
        onError(ShowErrorType.Dialog, error, onRetryClick = {
            checkCode(code)
        })
    }

    fun validateCode(code: String) {
        sendEvent(CodeEvent.ValidateCode(code.length == 4))
    }

    companion object {
        const val duration = 60000
        const val step = 1
    }
}