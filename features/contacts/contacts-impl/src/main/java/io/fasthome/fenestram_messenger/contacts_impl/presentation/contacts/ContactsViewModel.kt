/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ContactsViewModel(router: ContractRouter, requestParams: RequestParams) :
    BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

    override fun createInitialState(): ContactsState {
        return ContactsState()
    }

}