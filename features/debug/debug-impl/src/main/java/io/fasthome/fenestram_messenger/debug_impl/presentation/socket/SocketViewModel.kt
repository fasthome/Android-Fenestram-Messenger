package io.fasthome.fenestram_messenger.debug_impl.presentation.socket

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.debug_impl.presentation.debug.DebugNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

class SocketViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    val interactor: Interactors
) : BaseViewModel<SocketState, SocketEvent>(router, requestParams) {

    class Interactors(
        val messengerInteractor: MessengerInteractor
    )

    private val debugLauncher = registerScreen(DebugNavigationContract) {}

    override fun createInitialState() = SocketState()

    fun onSendMessageClicked() {
        viewModelScope.launch {
            interactor.messengerInteractor.postChats(name, users)
            interactor.messengerInteractor.sendMessage(id, message, type)
            interactor.messengerInteractor.getChats(limit, page)
            interactor.messengerInteractor.getChatById(id)
        }
    }

    fun getMessageById() {
        viewModelScope.launch {
            interactor.messengerInteractor.getMessagesFromChat(id).collectLatest {
                if (it != null) {
                    it.text?.let { it1 -> Log.d("Message", it1) }
                }
            }
        }
    }

    fun onBackClicked() {
        debugLauncher.launch(NoParams)
    }

    fun closeSocket() {
        interactor.messengerInteractor.closeSocket()
    }

    companion object {
        const val name = "TestChat"
        val users = listOf(2)

        const val limit = 10
        const val page = 1

        const val id = 13L

        const val message = "Hello World!"
        const val type = "test"
    }

}