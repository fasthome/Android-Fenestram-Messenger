/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class ContactsViewModel(
    router: ContractRouter, requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
) :
    BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

    init {
        requestPermissionAndLoadContacts()
    }

    private fun requestPermissionAndLoadContacts() {
        viewModelScope.launch {
            val permissionGranted = permissionInterface.request(Manifest.permission.READ_CONTACTS)

            if (permissionGranted) {
                //todo
            }

        }
    }

    override fun createInitialState(): ContactsState {
        return ContactsState(listOf())
    }

    fun fetchContacts() {
        val contacts = listOf(
            ContactsViewItem(
                id = 1,
                name = "John",
                avatar = R.drawable.bg_account_circle,
                newMessageVisibility = 8
            ),
            ContactsViewItem(
                id = 2,
                name = "Maria",
                avatar = R.drawable.bg_account_circle,
                newMessageVisibility = 8
            ),
            ContactsViewItem(
                id = 3,
                name = "Henry",
                avatar = R.drawable.bg_account_circle,
                newMessageVisibility = 0
            ),
        )

        updateState { ContactsState(contacts) }
    }

}