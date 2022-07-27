package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ConversationViewModel(
    router: ContractRouter,
    requestParams: RequestParams
) : BaseViewModel<ConversationState, ConversationEvent>(router, requestParams) {

    private var currentConversation: List<ConversationViewItem> = listOf()

    fun exitToMessenger() = exitWithoutResult()


    override fun createInitialState(): ConversationState {
        return ConversationState(listOf())
    }


    fun addMessageToConversation(mess: String, time: String) {
        currentConversation = currentConversation + listOf(ConversationViewItem(mess, time, false))
        updateState { ConversationState(currentConversation) }
    }

}