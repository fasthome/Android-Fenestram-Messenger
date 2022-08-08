package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.toConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.getOrThrow
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConversationViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ConversationNavigationContract.Params,
    private val features: Features,
    private val messengerInteractor: MessengerInteractor,
) : BaseViewModel<ConversationState, ConversationEvent>(router, requestParams) {

    init {
        viewModelScope.launch {
            val selfUserId = features.authFeature.getUserId().getOrNull()
            if(selfUserId == null){
                features.authFeature.logout()
                return@launch
            }
            if(params.chat.id == null){
                messengerInteractor.postChats(name = "123", users = listOf(selfUserId, params.chat.user.id)).onSuccess {
                    if(it is PostChatsResult.Success) {

                    }
                }
                return@launch
            }
            messengerInteractor.getMessagesFromChat(params.chat.id).collectLatest { message ->
                updateState { state ->
                    state.copy(
                        messages = state.messages.plus(
                            message.toConversationViewItem(selfUserId)
                        )
                    )
                }
            }
        }
    }

    class Features(
        val profileGuestFeature: ProfileGuestFeature,
        val authFeature : AuthFeature
    )

    private val profileGuestLauncher =
        registerScreen(features.profileGuestFeature.profileGuestNavigationContract)

    fun exitToMessenger() = exitWithoutResult()


    override fun createInitialState(): ConversationState {
        return ConversationState(
            messages = listOf(),
            userName = PrintableText.Raw(params.chat.user.name),
            userOnline = false
        )
    }


    fun addMessageToConversation(mess: String, time: String) {
//        updateState {
//            it.copy(
//                messages = it.messages.plus(
//                    ConversationViewItem(
//                        mess,
//                        time,
//                        false
//                    )
//                )
//            )
//        }
        viewModelScope.launch {
            messengerInteractor.sendMessage(
                id = params.chat.id ?: return@launch,
                text = mess,
                type = "text"
            ).getOrThrow()
        }

    }

    fun onUserClicked() {
        profileGuestLauncher.launch(NoParams)
    }

    fun closeSocket() {
        messengerInteractor.closeSocket()
    }
}