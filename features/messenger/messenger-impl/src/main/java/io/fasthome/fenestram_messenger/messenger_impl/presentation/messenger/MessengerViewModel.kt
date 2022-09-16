/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.CreateGroupChatContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.mapper.toMessengerViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MessengerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val messengerInteractor: MessengerInteractor,
    profileGuestFeature: ProfileGuestFeature,
    private val loadDataHelper: PagingDataViewModelHelper,
) : BaseViewModel<MessengerState, MessengerEvent>(router, requestParams) {

    private val conversationlauncher = registerScreen(ConversationNavigationContract) {
        exitWithoutResult()
    }

    private val createGroupChatLauncher = registerScreen(CreateGroupChatContract)
    private val profileGuestLauncher = registerScreen(profileGuestFeature.profileGuestNavigationContract)

    val items = loadDataHelper.getDataFlow(
        getItems = {
            messengerInteractor.getMessengerPageItems()
        },
        getCachedSelectedId = { null },
        mapDataItem = {
            toMessengerViewItem(it)
        },
        getItemId = { it.id },
        getItem = { null }
    ).cachedIn(viewModelScope)


    fun fetchNewMessages() {
        viewModelScope.launch {
            subscribeMessages()
        }
    }

    fun launchConversation(messangerViewItem: MessengerViewItem) {
        conversationlauncher.launch(
            ConversationNavigationContract.Params(
                chat = messangerViewItem.originalChat
            )
        )
    }

    override fun createInitialState(): MessengerState {
        return MessengerState(listOf(), listOf())
    }

    fun onCreateChatClicked() {
        createGroupChatLauncher.launch(NoParams)
    }

    fun onProfileClicked(messengerViewItem: MessengerViewItem) {
        profileGuestLauncher.launch(
            ProfileGuestFeature.ProfileGuestParams(
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
                loadDataHelper.invalidateSource()
            }
            .launchIn(viewModelScope)
    }

    fun unsubscribeMessages() {
        messengerInteractor.closeSocket()
    }

}