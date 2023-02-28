/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.core.exceptions.EmptySearchException
import io.fasthome.fenestram_messenger.core.exceptions.PermissionDeniedException
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.logic.MessengerInteractor
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info.CreateInfoContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.mapper.mapToContactViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.LoadingState
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.coroutines.launch

class CreateGroupChatViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val contactsFeature: ContactsFeature,
    private val messengerInteractor: MessengerInteractor,
    private val params: CreateGroupChatContract.Params,
    private val permissionInterface: PermissionInterface,
) : BaseViewModel<CreateGroupChatState, CreateGroupChatEvent>(router, requestParams) {

    private val createInfoLauncher = registerScreen(CreateInfoContract)
    private var originalContacts = listOf<Contact>()

    private val conversationLauncher = registerScreen(ConversationNavigationContract) { }

    override fun createInitialState(): CreateGroupChatState {
        return CreateGroupChatState(LoadingState.None, listOf(), false, params.isGroupChat)
    }

    fun checkPermissionsAndLoadContacts() {
        viewModelScope.launch {
            updateState { state ->
                state.copy(loadingState = LoadingState.Loading)
            }

            val readPermissionGranted = permissionInterface.request(Manifest.permission.READ_CONTACTS)
            if (!readPermissionGranted) {
                updateState { state ->
                    state.copy(
                        loadingState = LoadingState.Error(
                            error = ErrorInfo.createEmpty(),
                            throwable = PermissionDeniedException()
                        )
                    )
                }
                return@launch
            }

            when (val result = contactsFeature.getContactsAndUploadContacts()) {
                is CallResult.Error -> {
                    onError(
                        ShowErrorType.Dialog,
                        result.error,
                        { exitWithoutResult() },
                        { checkPermissionsAndLoadContacts() })

                    updateState { state ->
                        state.copy(loadingState = LoadingState.None)
                    }
                }
                is CallResult.Success -> {
                    val id = messengerInteractor.getUserId()
                    originalContacts = result.data.filter { contact ->
                        contact.user != null && contact.userId != id
                    }
                    updateState { state ->
                        state.copy(loadingState = LoadingState.Success(data = originalContacts.map(::mapToContactViewItem)))
                    }
                }
            }
        }
    }

    fun onContactClicked(contactViewItem: ContactViewItem) {
        updateState { state ->
            val newList = if (contactViewItem.isSelected) {
                state.addedContacts.filter { it.userId != contactViewItem.userId }
            } else {
                if (params.isGroupChat)
                    state.addedContacts.plus(contactViewItem)
                else
                    state.addedContacts.dropLast(1).plus(contactViewItem)
            }
            state.copy(addedContacts = newList, needScroll = true)
        }
    }

    fun onContactRemoveClick(contactViewItem: ContactViewItem) {
        updateState { state ->
            val newList = state.addedContacts.filter {
                it.userId != contactViewItem.userId
            }
            state.copy(addedContacts = newList, needScroll = false)
        }
    }

    fun onNextClicked() {
        if (currentViewState.addedContacts.isEmpty()) {
            showPopup(PrintableText.StringResource(R.string.messenger_select_hint))
            return
        }
        val contacts = currentViewState.addedContacts.mapNotNull { viewItem ->
            originalContacts.find { contact ->
                viewItem.userId == contact.userId
            }
        }

        if (params.isGroupChat)
            createInfoLauncher.launch(CreateInfoContract.Params(contacts = contacts))
        else {
            router.backTo(null)
            conversationLauncher.launch(
                ConversationNavigationContract.Params(
                    chat = Chat(
                        name = contacts[0].userName ?: "",
                        users = listOf(contacts[0].user!!.id),
                        isGroup = false,
                        time = null,
                        messages = listOf(),
                        avatar = contacts[0].user!!.avatar,
                        id = null,
                        pendingMessages = 0
                    ),
                )
            )
        }
    }

    fun filterContacts(text: String) {
        val filteredContacts = if (text.isEmpty()) {
            originalContacts
        } else {
            originalContacts.filter {
                it.userName?.contains(text.trim(), true) ?: false
            }
        }
        updateState { state ->
            state.copy(
                loadingState = LoadingState.Success(filteredContacts.map(::mapToContactViewItem))
            )
        }
        if (filteredContacts.isEmpty()) {
            updateState { state ->
                state.copy(
                    loadingState = LoadingState.Error(
                        error = ErrorInfo(
                            PrintableText.EMPTY,
                            PrintableText.StringResource(
                                io.fasthome.fenestram_messenger.uikit.R.string.contacts_empty_view,
                                text
                            )
                        ),
                        throwable = EmptySearchException()
                    )
                )
            }
        }
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

    fun onOtherError(throwable: Throwable) {
        onError(
            showErrorType = ShowErrorType.Dialog,
            throwable = throwable,
            { exitWithoutResult() },
            { checkPermissionsAndLoadContacts() }
        )
    }

}