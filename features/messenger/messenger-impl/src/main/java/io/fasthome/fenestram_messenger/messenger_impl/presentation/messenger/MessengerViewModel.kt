/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationState
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.Params
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class MessengerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    val messengerInteractor: MessengerInteractor
) : BaseViewModel<MessengerState, MessengerEvent>(router, requestParams) {

    private val conversationlauncher = registerScreen(ConversationNavigationContract) {
        exitWithoutResult()
    }

    fun launchConversation(chatId : Long) {
        val chat = currentViewState.chats.find { it.id == chatId } ?: return
        conversationlauncher.launch(Params(
            chat = chat
        ))
    }

    override fun createInitialState(): MessengerState {
        return MessengerState(listOf(), listOf())
    }

    fun fetchChats(){
        viewModelScope.launch {
            messengerInteractor.getChats(10, 1).withErrorHandled { result->
                when (result) {
                    is GetChatsResult.Success -> {
                        val chats = result.chats.map {
                            MessengerViewItem(
                                id = it.id,
                                avatar = 0,
                                name = it.user.name,
                                newMessages = 0
                            )
                        }
                        updateState {
                            it.copy(messengerViewItems = chats, chats = result.chats)
                        }
                    }
                }
            }
        }
    }

}