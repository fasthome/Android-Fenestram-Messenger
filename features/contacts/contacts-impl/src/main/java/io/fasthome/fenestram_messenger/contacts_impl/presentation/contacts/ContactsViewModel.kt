/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ContactsViewModel(router: ContractRouter, requestParams: RequestParams) :
    BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

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