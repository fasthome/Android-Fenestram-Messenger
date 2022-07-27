/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationState
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class MessengerViewModel(
    router: ContractRouter,
    requestParams: RequestParams
) : BaseViewModel<MessengerState, MessengerEvent>(router, requestParams) {

    private var currentChats: List<MessengerViewItem> = listOf()

    private val conversationlauncher = registerScreen(ConversationNavigationContract)

    fun launchConversation() {
        conversationlauncher.launch(NoParams)
    }

    override fun createInitialState(): MessengerState {
        currentChats = currentChats + listOf(MessengerViewItem(0, 0, "Bob", 0))
        currentChats = currentChats + listOf(MessengerViewItem(1, 0, "Rob", 0))
        currentChats = currentChats + listOf(MessengerViewItem(2, 0, "Tony", 0))
        return MessengerState(currentChats)
    }


}