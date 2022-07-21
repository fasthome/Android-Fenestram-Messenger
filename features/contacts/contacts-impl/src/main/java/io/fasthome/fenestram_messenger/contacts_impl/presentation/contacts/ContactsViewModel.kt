/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.Manifest
import android.util.Log
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.ContactAddNavigationContract
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.model.ContactAddResult
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.contacts_impl.presentation.util.ContactsLoader
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.LoadingState
import io.fasthome.fenestram_messenger.util.dataOrNull
import kotlinx.coroutines.launch

class ContactsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
    private val contactsLoader: ContactsLoader
) : BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

    init {
        requestPermissionAndLoadContacts()
    }

    private val addContactLauncher = registerScreen(ContactAddNavigationContract) { result ->
        when(result) {
            is ContactAddResult.Success -> requestPermissionAndLoadContacts()
            is ContactAddResult.Canceled -> sendEvent(ContactsEvent.ContactAddCancelled)
        }
    }

    private var currentContacts: List<ContactsViewItem> = listOf()

    private fun requestPermissionAndLoadContacts() {
        viewModelScope.launch {
            val permissionGranted = permissionInterface.request(Manifest.permission.READ_CONTACTS)

            if (permissionGranted) {
                fetchContacts()
            }
        }
    }

    override fun createInitialState(): ContactsState {
        return ContactsState(LoadingState.None)
    }

    private fun fetchContacts() {
        currentContacts = contactsLoader.onStartLoading()
        if (currentContacts.isEmpty()) {
            updateState {
                ContactsState(
                    LoadingState.Error(error = ErrorInfo.createEmpty())
                )
            }
        } else {
            updateState { ContactsState(LoadingState.Success(currentContacts)) }
        }
    }

    fun addContact() {
        addContactLauncher.launch(NoParams)
    }

    fun filterContacts(text: String) {
        val filteredContacts = currentContacts.filter {
            it.name.startsWith(text.trim(), true)
        }
        updateState { ContactsState(LoadingState.Success(filteredContacts)) }
    }

}