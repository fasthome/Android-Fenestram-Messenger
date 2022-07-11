/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class ContactsViewModel(
    router: ContractRouter, requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
    private val contactsLoader: ContactsLoader
) : BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

    init {
        requestPermissionAndLoadContacts()
    }

    var currentContacts: List<ContactsViewItem> = listOf()

    private fun requestPermissionAndLoadContacts() {
        viewModelScope.launch {
            val permissionGranted = permissionInterface.request(Manifest.permission.READ_CONTACTS)

            if (permissionGranted) {
                fetchContacts()
            }

        }
    }

    override fun createInitialState(): ContactsState {
        return ContactsState(listOf())
    }

    private fun fetchContacts() {
        currentContacts = contactsLoader.onStartLoading()
        updateState { ContactsState(currentContacts) }
    }

    fun addContact() {
        currentContacts =
            currentViewState.contacts + listOf(ContactsViewItem(0, 0, "New Contact", 0))
        updateState { ContactsState(currentContacts) }
    }

    fun filterContacts(text: String) {
        if (text.isEmpty()) {
            updateState { ContactsState(currentContacts) }
        } else {
            val filteredContacts = currentContacts.filter {
                it.name.startsWith(text, true)
            }
            updateState { ContactsState(filteredContacts) }
        }
    }

}