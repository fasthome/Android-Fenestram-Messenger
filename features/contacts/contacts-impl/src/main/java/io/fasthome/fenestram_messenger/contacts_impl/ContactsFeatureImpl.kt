/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl

import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.domain.logic.ContactsInteractor
import io.fasthome.fenestram_messenger.contacts_impl.domain.repo.ContactsRepo
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.ContactAddNavigationContract
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.ContactsNavigationContract
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.contract.map
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult

class ContactsFeatureImpl(private val contactsInteractor: ContactsInteractor) : ContactsFeature {
    override val contactsNavigationContract: NavigationContractApi<NoParams, NoResult> =
        ContactsNavigationContract
    override val contactAddNavigationContract: NavigationContractApi<ContactsFeature.Params, ContactsFeature.ContactAddResult> =
        ContactAddNavigationContract.map({
            ContactAddNavigationContract.Params(
                name = it.name,
                phone = it.phone
            )
        },
            resultMapper = {
                when (it) {
                    is ContactAddNavigationContract.ContactAddResult.Success -> ContactsFeature.ContactAddResult.Success
                    is ContactAddNavigationContract.ContactAddResult.Canceled -> ContactsFeature.ContactAddResult.Canceled
                }
            }
        )

    override suspend fun getContacts(): CallResult<List<Contact>> = contactsInteractor.getContacts()

    override suspend fun deleteContacts(contactIds: List<Long>): CallResult<Unit> =
        contactsInteractor.deleteContacts(contactIds)

    override suspend fun deleteAllContacts(): CallResult<Unit> =
        contactsInteractor.deleteAllContacts()

    override suspend fun getContactsAndUploadContacts(): CallResult<List<Contact>> =
        contactsInteractor.getContactsAndUploadContacts()
}