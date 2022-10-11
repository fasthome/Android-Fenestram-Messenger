/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.domain.logic.ContactsInteractor
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.ContactAddNavigationContract
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.mapper.ContactsMapper
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.LoadingState
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import kotlinx.coroutines.launch

class ContactsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
    private val contactsInteractor: ContactsInteractor,
    private val messengerFeature: MessengerFeature
) : BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

    init {
        requestPermissionAndLoadContacts()
    }

    private val addContactLauncher = registerScreen(ContactAddNavigationContract) { result ->
        when (result) {
            is ContactAddNavigationContract.ContactAddResult.Success -> requestPermissionAndLoadContacts()
            is ContactAddNavigationContract.ContactAddResult.Canceled -> sendEvent(ContactsEvent.ContactAddCancelled)
        }
    }

    private var originalContactsViewItem = mutableListOf<ContactsViewItem>()

    private val conversationLauncher =
        registerScreen(messengerFeature.conversationNavigationContract) { }

    @SuppressLint("MissingPermission")
    fun requestPermissionAndLoadContacts() {
        viewModelScope.launch {
            updateState { state ->
                state.copy(loadingState = LoadingState.Loading)
            }
            val permissionGranted = permissionInterface.request(Manifest.permission.READ_CONTACTS)

            if (permissionGranted) {
                contactsInteractor.getContactsAndUploadContacts()
                    .withErrorHandled(showErrorType = ShowErrorType.Dialog) { contacts ->
                        updateState { state ->
                            originalContactsViewItem =
                                ContactsMapper.contactsListToViewList(contacts).toMutableList()
                            if (originalContactsViewItem.isEmpty()) {
                                state.copy(loadingState = LoadingState.Error(error = ErrorInfo.createEmpty()))
                            } else {
                                state.copy(loadingState = LoadingState.Success(data = originalContactsViewItem))
                            }
                        }
                    }
            } else {
                updateState {
                    ContactsState(
                        LoadingState.Error(error = ErrorInfo.createEmpty()),
                        false
                    )
                }
            }
        }
    }

    override fun createInitialState(): ContactsState {
        return ContactsState(LoadingState.None, true)
    }

    fun addContact() {
        addContactLauncher.launch(ContactAddNavigationContract.Params(null, null))
    }

    fun filterContacts(text: String) {
        val filteredContacts = if (text.isEmpty()) {
            originalContactsViewItem
        } else {
            originalContactsViewItem.filter {
                if (it !is ContactsViewItem.Header) {
                    getPrintableRawText(it.name).contains(text.trim(), true)
                } else {
                    false
                }
            }
        }
        updateState { state ->
            state.copy(
                loadingState = LoadingState.Success(filteredContacts)
            )
        }
    }

    fun onContactClicked(contactsViewItem: ContactsViewItem) {
        if (contactsViewItem is ContactsViewItem.Api) {
            conversationLauncher.launch(
                MessengerFeature.Params(
                    userIds = listOf(contactsViewItem.userId),
                    chatName = getPrintableRawText(contactsViewItem.name),
                    avatar = contactsViewItem.avatar,
                    isGroup = false
                )
            )
        }
    }

}