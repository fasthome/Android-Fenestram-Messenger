/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import android.util.Log
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessengerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val messengerInteractor: MessengerInteractor,
    profileGuestFeature: ProfileGuestFeature,
    private val loadDataHelper: PagingDataViewModelHelper,
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
        mapDataItem = {
            toMessengerViewItem(it)
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
        return MessengerState(listOf(), listOf())
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
                loadDataHelper.invalidateSource()
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
}