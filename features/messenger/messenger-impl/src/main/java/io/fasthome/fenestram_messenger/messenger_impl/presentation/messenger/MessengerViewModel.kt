/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.CreateGroupChatContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.mapper.MessengerMapper
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MessengerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val messengerInteractor: MessengerInteractor,
    profileGuestFeature: ProfileGuestFeature,
    private val loadDataHelper: PagingDataViewModelHelper,
    private val messengerMapper: MessengerMapper
) : BaseViewModel<MessengerState, MessengerEvent>(router, requestParams) {

    private val conversationlauncher = registerScreen(ConversationNavigationContract) { result ->
        when (result) {
            is ConversationNavigationContract.Result.ChatDeleted -> {
                updateState {
                    val chats = mutableListOf<MessengerViewItem>()
                    currentViewState.messengerViewItems.forEach { item ->
                        if (item.id != result.id)
                            chats.add(item)
                    }
                    it.copy(messengerViewItems = chats)
                }
                loadDataHelper.invalidateSource()
            }
        }
    }

    private val createGroupChatLauncher = registerScreen(CreateGroupChatContract)
    private val profileGuestLauncher =
        registerScreen(profileGuestFeature.profileGuestNavigationContract) { result ->
            when (result) {
                is ProfileGuestFeature.ProfileGuestResult.ChatDeleted -> {
                    updateState {
                        val chats = currentViewState.messengerViewItems.filter { item ->
                            item.id != result.id
                        }
                        it.copy(messengerViewItems = chats)
                    }
                    loadDataHelper.invalidateSource()
                }
                else -> {}
            }
        }

    private var _query = ""
    val items = loadDataHelper.getDataFlow(
        getItems = {
            messengerInteractor.getMessengerPageItems(_query)
        },
        getCachedSelectedId = { null },
        mapDataItem = messengerMapper::toMessengerViewItem,
        getItemId = { it.id },
        getItem = { null }
    ).cachedIn(viewModelScope)

    fun filterChats(query: String) {
        _query = query.trim()
    }

    fun fetchNewMessages() {
        loadDataHelper.invalidateSource()
        viewModelScope.launch {
            subscribeMessages()
            subscribeMessageActions()
        }
    }

    fun launchConversation(messangerViewItem: MessengerViewItem) {
        conversationlauncher.launch(
            ConversationNavigationContract.Params(
                fromContacts = false,
                chat = messangerViewItem.originalChat
            )
        )
    }

    override fun createInitialState(): MessengerState {
        return MessengerState(listOf(), listOf(), newMessagesCount = 0)
    }

    fun onCreateChatClicked() {
        sendEvent(MessengerEvent.CreateChatEvent)
    }

    fun createChatClicked(isGroup: Boolean) {
        createGroupChatLauncher.launch(CreateGroupChatContract.Params(isGroup))
    }

    fun onProfileClicked(messengerViewItem: MessengerViewItem) {
        viewModelScope.launch {
            val chatId = messengerViewItem.originalChat.id
            if (chatId != null)
                messengerInteractor.getChatById(chatId).onSuccess { chat ->
                    profileGuestLauncher.launch(
                        ProfileGuestFeature.ProfileGuestParams(
                            id = chatId,
                            userName = chat.chatName,
                            userNickname = chat.chatUsers.firstOrNull { it.id != messengerInteractor.getUserId() }?.nickname
                                ?: "",
                            userAvatar = chat.avatar,
                            chatParticipants = chat.chatUsers,
                            isGroup = messengerViewItem.isGroup,
                            userPhone = chat.chatUsers.firstOrNull { it.id != messengerInteractor.getUserId() }?.phone
                                ?: "",
                            editMode = false
                        )
                    )
                }
        }
    }

    private suspend fun subscribeMessages() {
        messengerInteractor.getNewMessages()
            .collectWhenViewActive()
            .onEach { message ->
                loadDataHelper.invalidateSource()

                updateState { state ->
                    state.copy(newMessagesCount = state.newMessagesCount + 1)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun subscribeMessageActions() {
        messengerInteractor.messageActionsFlow
            .collectWhenViewActive()
            .onEach { messageAction ->
                viewModelScope.launch(context = NonCancellable) {
                    if (!messengerMapper.messageActions.contains(messageAction)) {
                        messengerMapper.messageActions.add(messageAction)
                        loadDataHelper.invalidateSource()
                        delay(1500)
                        messengerMapper.messageActions.remove(messageAction)
                        loadDataHelper.invalidateSource()
                    }
                }
            }
            .launchIn(viewModelScope)
    }


    fun unsubscribeMessages() {
        messengerInteractor.closeSocket()
    }

    fun onChatDelete(id: Long) {
        sendEvent(MessengerEvent.DeleteChatEvent(id))
    }

    fun deleteChat(id: Long) {
        viewModelScope.launch {
            if (messengerInteractor.deleteChat(id).successOrSendError() != null)
                updateState {
                    val chats = mutableListOf<MessengerViewItem>()
                    currentViewState.messengerViewItems.forEach { item ->
                        if (item.id != id)
                            chats.add(item)
                    }
                    it.copy(messengerViewItems = chats)
                }
            loadDataHelper.invalidateSource()
        }
    }

    fun onReadMessages() {
        updateState { state ->
            state.copy(newMessagesCount = 0)
        }
    }
}