/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.data.repo_impl

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.data.service.ContactsService
import io.fasthome.fenestram_messenger.contacts_impl.domain.repo.ContactsRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult
import io.fasthome.fenestram_messenger.util.getOrThrow

class ContactsRepoImpl(private val authFeature: AuthFeature, private val contactsService: ContactsService) :
    ContactsRepo {

    override suspend fun getContacts(): List<Contact> =
        authFeature.getUsers().getOrThrow().map {
            Contact(
                id = it.id,
                phone = it.phone,
                userName = it.name,
                userId = it.id,
                user = null
            )
        }

    override suspend fun uploadContacts(contacts: List<Contact>): CallResult<Unit> = callForResult {
        contactsService.sendContacts(contacts)
    }

    override suspend fun loadContacts(): CallResult<List<Contact>> = callForResult {
        contactsService.getContacts()
    }

    override suspend fun deleteContacts(contactIds: List<Long>) = callForResult {
        contactsService.deleteContacts(contactIds)
    }

    override suspend fun updateContactName(name: String, phone: String) = callForResult {
        contactsService.updateContactName(name, phone)
    }

}