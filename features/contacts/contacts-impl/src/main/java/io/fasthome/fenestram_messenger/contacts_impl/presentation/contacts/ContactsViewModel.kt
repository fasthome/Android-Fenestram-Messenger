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
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.model.ContactAddResult
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.mapper.ContactsMapper
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.launch

class ContactsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
    private val contactsInteractor: ContactsInteractor,
    private val messengerFeature: MessengerFeature,
) : BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

    init {
        requestPermissionAndLoadContacts()
    }

    private val addContactLauncher = registerScreen(ContactAddNavigationContract) { result ->
        when (result) {
            is ContactAddResult.Success -> requestPermissionAndLoadContacts()
            is ContactAddResult.Canceled -> sendEvent(ContactsEvent.ContactAddCancelled)
        }
    }

    private var originalContacts = mutableListOf<ContactsViewItem>()

    private val conversationLauncher = registerScreen(messengerFeature.conversationNavigationContract)

    @SuppressLint("MissingPermission")
    fun requestPermissionAndLoadContacts() {
        viewModelScope.launch {
            updateState { state ->
                state.copy(loadingState = LoadingState.Loading)
            }
            val permissionGranted = permissionInterface.request(Manifest.permission.READ_CONTACTS)

            if (permissionGranted) {
                contactsInteractor.getContactsAndUploadContacts().withErrorHandled(showErrorType = ShowErrorType.Dialog) { contacts->
                    updateState { state ->
                        originalContacts = ContactsMapper.contactsListToViewList(contacts).toMutableList()
                        if (originalContacts.isEmpty()) {
                            state.copy(loadingState = LoadingState.Error(error = ErrorInfo.createEmpty()))
                        } else {
                            state.copy(loadingState = LoadingState.Success(data = originalContacts))
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
        addContactLauncher.launch(NoParams)
    }

    fun filterContacts(text: String) {
        val filteredContacts = if (text.isEmpty()) {
            originalContacts
        } else {
            currentViewState.loadingState.dataOrNull?.filter {
                if (it !is ContactsViewItem.Header) {
                    getPrintableRawText(it.name).startsWith(text.trim(), true)
                } else {
                    false
                }
            } ?: listOf()
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
                    isGroup = false
                )
            )
        }
    }

}