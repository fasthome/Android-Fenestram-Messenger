/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatChanges
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessageStatus
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.getSentStatus
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
    profileGuestFeature: ProfileGuestFeature,
    private val messengerInteractor: MessengerInteractor,
    private val loadDataHelper: PagingDataViewModelHelper,
    private val messengerMapper: MessengerMapper,
    private val params: MessengerNavigationContract.Params,
) : BaseViewModel<MessengerState, MessengerEvent>(router, requestParams) {

    private val conversationLauncher = registerScreen(ConversationNavigationContract) { result ->
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
            sendEvent(MessengerEvent.ProgressEvent(isProgress = true))
            messengerInteractor.getMessengerPageItems(_query, loadDataHelper.fromSocket)
        },
        getCachedSelectedId = { null },
        mapDataItem = {
            sendEvent(MessengerEvent.ProgressEvent(isProgress = false))
            return@getDataFlow messengerMapper.toMessengerViewItem(it)
        },
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

    fun launchConversation(messengerViewItem: MessengerViewItem) {
        if (params.chatSelectionMode) {
            /***
             * Если мы пересылаем сообщение, backStack должен быть очищен, чтобы не вернуться по кнопке назад в предыдущие чаты
             */
            router.backTo(null)
            conversationLauncher.launch(
                ConversationNavigationContract.Params(
                    fromContacts = false,
                    chat = messengerViewItem.originalChat,
                    forwardMessage = params.forwardMessage
                )
            )
        } else {
            conversationLauncher.launch(

                ConversationNavigationContract.Params(
                    fromContacts = false,
                    chat = messengerViewItem.originalChat
                )
            )
        }
    }

    override fun createInitialState(): MessengerState {
        return MessengerState(
            chats = listOf(),
            messengerViewItems = listOf(),
            newMessagesCount = 0,
            isSelectMode = params.chatSelectionMode,
            scrolledDown = false
        )
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
        messengerInteractor.getNewMessages(
            { onNewMessageStatus(it) },
            { onChatChangesCallback(it) })
            .collectWhenViewActive()
            .onEach { message ->
                loadDataHelper.invalidateSource()

                updateState { state ->
                    state.copy(newMessagesCount = state.newMessagesCount + 1)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onNewMessageStatus(messageStatus: MessageStatus) {
        updateState { state ->
            loadDataHelper.invalidateSource()
            state.copy(
                messengerViewItems = currentViewState.messengerViewItems.map {
                    if (it.id == messageStatus.messageId)
                        it.copy(sentStatus = getSentStatus(messageStatus.messageStatus))
                    else it
                }
            )
        }
    }

    private fun onChatChangesCallback(chatChanges: ChatChanges) {
        //TODO изменение имени и аватара чата
    }

    private fun subscribeMessageActions() {
        messengerInteractor.messageActionsFlow
            .collectWhenViewActive()
            .onEach { messageAction ->
                viewModelScope.launch(context = NonCancellable) {
                    if (!messengerMapper.messageActions.contains(messageAction)) {
                        messengerMapper.messageActions.add(messageAction)
                        loadDataHelper.fromSocket = true
                        loadDataHelper.invalidateSource()
                        delay(1500)
                        messengerMapper.messageActions.remove(messageAction)
                        loadDataHelper.fromSocket = true
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

    fun onScrolledDown(scrolledDown: Boolean) {
        if (currentViewState.scrolledDown != scrolledDown)
            updateState { state -> state.copy(scrolledDown = scrolledDown) }
    }

    fun exitToConversation() = exitWithoutResult()
}