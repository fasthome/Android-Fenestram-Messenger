/**
 * Created by Dmitry Popov on 16.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.data.service

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper.ContactsMapper
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsRequest
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsResponse
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.DeleteContactsRequest
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class ContactsService(clientFactory: NetworkClientFactory) {
    private val client = clientFactory.create()

    suspend fun getContacts(): List<Contact> =
        client
            .runGet<BaseResponse<List<ContactsResponse>>>(
                path = "api/v1/contacts"
            )
            .requireData()
            .let(ContactsMapper::responseToContactsList)

    suspend fun sendContacts(contacts: List<Contact>) {
        return client
            .runPost<List<ContactsRequest>, BaseResponse<Unit>>(
                path = "api/v1/contacts",
                body = ContactsMapper.contactListToRequest(contacts)
            )
            .requireData()
    }

    suspend fun deleteContacts(contactIds: List<Long>) {
        val body = DeleteContactsRequest(
            contacts = contactIds
        )
        return client.runDelete<DeleteContactsRequest, BaseResponse<Unit>>(
            path = "api/v1/contacts",
            body = body
        ).requireData()
    }

}
