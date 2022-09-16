package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessagesPage
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.createMessage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.toConversationItems
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.toConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper.Companion.PAGE_SIZE
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*

class ConversationViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ConversationNavigationContract.Params,
    private val features: Features,
    private val messengerInteractor: MessengerInteractor,
) : BaseViewModel<ConversationState, ConversationEvent>(router, requestParams) {

    private var chatId = params.chat.id
    private var chatUsers = listOf<User>()
    private var selfUserId: Long? = null
    private var loadItemsJob by switchJob()
    private var lastPage: MessagesPage? = null

    fun loadItems() {
        loadItemsJob = viewModelScope.launch {
            lastPage?.let {
                if (it.total <= PAGE_SIZE) {
                    return@launch
                }
            }

            messengerInteractor.getChatPageItems(chatId ?: return@launch).onSuccess {
                lastPage = it
                updateState { state ->
                    state.copy(
                        messages = state.messages.plus(
                            it.messages.toConversationItems(
                                selfUserId = selfUserId!!,
                                isGroup = params.chat.isGroup,
                            )
                        )
                    )
                }
            }
        }
    }

    fun fetchMessages() {
        viewModelScope.launch {
            selfUserId = features.authFeature.getUserId().getOrNull()
            if (selfUserId == null) {
                features.authFeature.logout()
                return@launch
            }

            if (params.chat.id == null) {
                messengerInteractor.postChats(
                    name = params.chat.name,
                    users = params.chat.users,
                    isGroup = params.chat.isGroup
                ).withErrorHandled {
                    chatId = it.chatId
                }
            }
            subscribeMessages(
                chatId ?: params.chat.id ?: return@launch,
                selfUserId ?: return@launch
            )
        }
    }

    class Features(
        val profileGuestFeature: ProfileGuestFeature,
        val authFeature: AuthFeature
    )

    private val profileGuestLauncher =
        registerScreen(features.profileGuestFeature.profileGuestNavigationContract) { result ->
            when (result) {
                is ProfileGuestFeature.ProfileGuestResult.ChatDeleted ->
                    exitWithResult(
                        ConversationNavigationContract.createResult(
                            ConversationNavigationContract.Result.ChatDeleted(result.id)
                        )
                    )
            }
        }

    fun exitToMessenger() = exitWithoutResult()

    override fun createInitialState(): ConversationState {
        return ConversationState(
            messages = mapOf(),
            userName = PrintableText.Raw(params.chat.name),
            userOnline = false,
            isChatEmpty = false,
            avatar = params.chat.avatar ?: ""
        )
    }

    fun addMessageToConversation(mess: String) {
        viewModelScope.launch {
            val tempMessage = createMessage(mess)
            val messages = currentViewState.messages

            updateState { state ->
                state.copy(
                    messages = mapOf(tempMessage.localId to tempMessage).plus(messages)
                )
            }

            when (messengerInteractor.sendMessage(
                id = chatId ?: return@launch,
                text = mess,
                type = "text",
                localId = tempMessage.localId
            )) {
                is CallResult.Error -> {
                    updateStatus(tempMessage, SentStatus.Error)
                }
                is CallResult.Success -> {
                    updateStatus(tempMessage, SentStatus.Sent)
                }
            }
        }
        sendEvent(ConversationEvent.MessageSent)
    }

    private fun updateStatus(tempMessage : ConversationViewItem.Self, status: SentStatus){
        updateState { state ->
            val newMessages = state.messages.toMutableMap()
            newMessages[tempMessage.localId] = tempMessage.copy(sentStatus = status)
            state.copy(
                messages = newMessages
            )
        }
    }

    fun onUserClicked() {
        profileGuestLauncher.launch(
            ProfileGuestFeature.ProfileGuestParams(
                id = chatId,
                userName = params.chat.name,
                userNickname = "",
                userAvatar = params.chat.avatar ?: "",
                chatParticipants = chatUsers,
                isGroup = params.chat.isGroup
            )
        )
    }

    fun closeSocket() {
        messengerInteractor.closeSocket()
    }

    private suspend fun subscribeMessages(chatId: Long, selfUserId: Long) {
        loadItems()
        messengerInteractor.getMessagesFromChat(chatId, selfUserId)
            .flowOn(Dispatchers.Main)
            .onEach { message ->
                updateState { state ->
                    state.copy(
                        messages = mapOf(
                            UUID.randomUUID().toString() to message.toConversationViewItem(
                                selfUserId = selfUserId,
                                isGroup = params.chat.isGroup,
                            )
                        ).plus(state.messages)
                    )
                }
                sendEvent(ConversationEvent.MessageSent)
            }
            .launchIn(viewModelScope)
        messengerInteractor.getChatById(chatId).onSuccess {
            chatUsers = it.chatUsers
        }
    }

    fun onGroupProfileClicked(item: ConversationViewItem.Group) {
        profileGuestLauncher.launch(
            ProfileGuestFeature.ProfileGuestParams(
                id = item.id,
                userName = getPrintableRawText(item.userName),
                userNickname = "",
                userAvatar = item.avatar,
                chatParticipants = listOf(),
                isGroup = false
            )
        )
    }

    fun onOpenMenu() {
        sendEvent(ConversationEvent.OpenMenuEvent)
    }

    fun showDialog() {
        chatId?.let { sendEvent(ConversationEvent.ShowDialog(it)) }
    }

    fun deleteChat(id: Long) {
        viewModelScope.launch {
            if (messengerInteractor.deleteChat(id).successOrSendError() != null)
                exitWithResult(
                    ConversationNavigationContract.createResult(
                        ConversationNavigationContract.Result.ChatDeleted(id)
                    )
                )
        }
    }

}