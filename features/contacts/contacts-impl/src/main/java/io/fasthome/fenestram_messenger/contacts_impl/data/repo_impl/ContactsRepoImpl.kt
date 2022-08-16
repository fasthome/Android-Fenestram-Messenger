/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.data.repo_impl

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.domain.repo.ContactsRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.getOrThrow
import io.fasthome.fenestram_messenger.util.onSuccess

class ContactsRepoImpl(private val authFeature: AuthFeature) : ContactsRepo {

    override suspend fun getContacts(): List<Contact> =
        authFeature.getUsers().getOrThrow().map {
            Contact(
                phone = it.phone,
                userName = it.name,
                userId = it.id
            )
        }


}