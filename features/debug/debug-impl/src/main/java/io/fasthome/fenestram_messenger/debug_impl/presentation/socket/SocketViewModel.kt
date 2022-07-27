package io.fasthome.fenestram_messenger.debug_impl.presentation.socket

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.debug_impl.presentation.debug.DebugNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

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

    fun onBackClicked() {
        debugLauncher.launch(NoParams)
    }

    companion object {
        const val name = "TestChat"
        val users = listOf(2)

        const val limit = 10
        const val page = 1

        const val id = 13

        const val message = "Hello World!"
        const val type = "test"
    }

}