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
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.LoadingState
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
        return ContactsState(currentContacts, LoadingState.None)
    }

    private fun fetchContacts() {
        currentContacts = contactsLoader.onStartLoading()
        if (currentContacts.isEmpty()) {
            updateState {
                ContactsState(
                    currentContacts,
                    LoadingState.Error(error = ErrorInfo.createEmpty())
                )
            }
        } else {
            updateState { ContactsState(currentContacts, LoadingState.Success(currentContacts)) }
        }
    }

    fun addContact() {
        currentContacts =
            currentViewState.contacts + listOf(ContactsViewItem(0, 0, "New Contact", 0))
        updateState { ContactsState(currentContacts, LoadingState.Success(currentContacts)) }
    }

    fun filterContacts(text: String) {
        val filteredContacts = currentContacts.filter {
            it.name.startsWith(text.trim(), true)
        }
        updateState { ContactsState(filteredContacts, LoadingState.Success(filteredContacts)) }
    }

}