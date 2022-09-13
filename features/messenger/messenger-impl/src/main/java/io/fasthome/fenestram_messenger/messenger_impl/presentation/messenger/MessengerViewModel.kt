/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.toConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.CreateGroupChatContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.mapper.toMessengerViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MessengerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val messengerInteractor: MessengerInteractor,
    private val authFeature: AuthFeature,
    profileGuestFeature: ProfileGuestFeature
) : BaseViewModel<MessengerState, MessengerEvent>(router, requestParams) {

    private val conversationlauncher = registerScreen(ConversationNavigationContract) { result ->
        when(result){
            is ConversationNavigationContract.Result.ChatDeleted -> { fetchChats() }
        }
    }

    private val createGroupChatLauncher = registerScreen(CreateGroupChatContract)
    private val profileGuestLauncher = registerScreen(profileGuestFeature.profileGuestNavigationContract) { result ->
        when(result){
            is ProfileGuestFeature.ProfileGuestResult.ChatDeleted -> fetchChats()
            else -> {}
        }
    }

    fun fetchNewMessages() {
        viewModelScope.launch {
            subscribeMessages()
        }
    }

    fun launchConversation(chatId: Long) {
        val chat = currentViewState.chats.find { it.id == chatId } ?: return
        conversationlauncher.launch(
            ConversationNavigationContract.Params(
                chat = chat
            )
        )
    }

    override fun createInitialState(): MessengerState {
        return MessengerState(listOf(), listOf())
    }

    fun fetchChats() {
        viewModelScope.launch {
            messengerInteractor.getChats(
                selfUserId = authFeature.getUserId().getOrNull() ?: return@launch,
                limit = 50,
                page = 1
            ).withErrorHandled(
                showErrorType = ShowErrorType.Dialog
            ) { result ->
                when (result) {
                    is GetChatsResult.Success -> {
                        val chats = result.chats
                            .sortedByDescending {
                                val millis = it.time?.toInstant()?.toEpochMilli()
                                millis
                            }
                            .mapNotNull(::toMessengerViewItem)
                        updateState {
                            it.copy(messengerViewItems = chats, chats = result.chats)
                        }
                    }
                }
            }
        }
    }

    fun onCreateChatClicked() {
        createGroupChatLauncher.launch(NoParams)
    }

    fun onProfileClicked(messengerViewItem: MessengerViewItem) {
        profileGuestLauncher.launch(
            ProfileGuestFeature.ProfileGuestParams(
                id = messengerViewItem.id,
                userName = getPrintableRawText(messengerViewItem.name),
                userNickname = "",
                userAvatar = messengerViewItem.profileImageUrl ?: "",
                listOf(),
                false
            )
        )
    }

    private suspend fun subscribeMessages() {
        messengerInteractor.getNewMessages()
            .collectWhenViewActive()
            .onEach { message ->
                fetchChats()
            }
            .launchIn(viewModelScope)
    }

    fun unsubscribeMessages() {
        messengerInteractor.closeSocket()
    }

}