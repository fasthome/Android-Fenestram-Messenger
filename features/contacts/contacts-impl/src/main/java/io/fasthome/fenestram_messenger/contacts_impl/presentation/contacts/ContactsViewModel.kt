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
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.LoadingState
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class ContactsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
    private val contactsInteractor: ContactsInteractor,
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

    @SuppressLint("MissingPermission")
    fun requestPermissionAndLoadContacts() {
        viewModelScope.launch {
            val permissionGranted = permissionInterface.request(Manifest.permission.READ_CONTACTS)

            if (permissionGranted) {
                contactsInteractor.getContactsAndUploadContacts().onSuccess {
                    updateState { state ->
                        state.copy(loadingState = LoadingState.Success(data = it.map(ContactsMapper::contactToViewItem)))
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

    private fun fetchContacts() {
//        contactsLoaderJob = contactsLoaderScope.launch {
//            currentContacts = contactsLoader.fetchLocalContacts()
//            if (currentContacts.isEmpty()) {
//                updateState {
//                    ContactsState(
//                        LoadingState.Error(error = ErrorInfo.createEmpty()),
//                        true
//                    )
//                }
//            } else {
//                updateState { ContactsState(LoadingState.Success(currentContacts), true) }
//            }
//        }
    }

    fun addContact() {
        addContactLauncher.launch(NoParams)
    }

    fun filterContacts(text: String) {
//        val filteredContacts = currentContacts.filter {
//            it.name.startsWith(text.trim(), true)
//        }
//        updateState { ContactsState(LoadingState.Success(filteredContacts), true) }
    }

    fun onContactClicked(contactsViewItem: ContactsViewItem) {
//        conversationLauncher.launch(
//            MessengerFeature.Params(
//                userId = contactsViewItem.id,
//                userName = contactsViewItem.name
//            )
//        )
    }

}