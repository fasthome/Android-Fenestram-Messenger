package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.toConversationViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.getOrThrow
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

    fun fetchMessages() {
        viewModelScope.launch {
            val selfUserId = features.authFeature.getUserId().getOrNull()
            if (selfUserId == null) {
                features.authFeature.logout()
                return@launch
            }

            if (params.chat.id == null) {
                messengerInteractor.postChats(name = params.chat.name, users = params.chat.users, isGroup = false)
                    .withErrorHandled {
                        if (it is PostChatsResult.Success) {
                            chatId = it.chatId
                        }
                    }
            }
            subscribeMessages(chatId ?: return@launch, selfUserId)
        }
    }

    class Features(
        val profileGuestFeature: ProfileGuestFeature,
        val authFeature: AuthFeature
    )

    private val profileGuestLauncher =
        registerScreen(features.profileGuestFeature.profileGuestNavigationContract)

    fun exitToMessenger() = exitWithoutResult()


    override fun createInitialState(): ConversationState {
        return ConversationState(
            messages = listOf(),
            userName = PrintableText.Raw(params.chat.name),
            userOnline = false,
            isChatEmpty = false
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
                userName = params.chat.name,
                userNickname = "nickname"
            )
        )
    }

    fun closeSocket() {
        messengerInteractor.closeSocket()
    }

    private suspend fun subscribeMessages(chatId: Long, selfUserId: Long) {
        messengerInteractor.getMessagesFromChat(chatId)
            .collectWhenViewActive()
            .onEach { messages ->
                updateState { state ->
                    state.copy(
                        messages = state.messages.plus(
                            messages.map {
                                it.toConversationViewItem(selfUserId)
                            }
                        ),
                        isChatEmpty = messages.isEmpty()
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}