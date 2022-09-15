package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.toConversationItems
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
            messages = listOf(),
            userName = PrintableText.Raw(params.chat.name),
            userOnline = false,
            isChatEmpty = false,
            avatar = params.chat.avatar ?: ""
        )
    }


    fun addMessageToConversation(mess: String) {
        viewModelScope.launch {
            messengerInteractor.sendMessage(
                id = chatId ?: return@launch,
                text = mess,
                type = "text"
            ).withErrorHandled(
                showErrorType = ShowErrorType.Dialog,
                onSuccess = {})
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
        messengerInteractor.getMessagesFromChat(chatId)
            .flowOn(Dispatchers.Main)
            .onEach { messages ->
                updateState { state ->
                    state.copy(
                        messages = state.messages
                            .plus(
                                messages.toConversationItems(
                                    selfUserId,
                                    params.chat.isGroup,
                                    lastMessage = state.messages.lastOrNull(),
                                    messagesEmpty = state.messages.isEmpty()
                                )
                            ),
                        isChatEmpty = messages.isEmpty()
                    )
                }
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