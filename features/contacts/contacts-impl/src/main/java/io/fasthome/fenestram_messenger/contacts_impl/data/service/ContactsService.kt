/**
 * Created by Dmitry Popov on 16.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.data.service

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper.ContactsMapper
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsResponse
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.DeleteContactsRequest
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.SendContactsRequest
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.UpdateContactNameRequest
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class ContactsService(
    clientFactory: NetworkClientFactory,
    private val contactsMapper: ContactsMapper,
    private val environment: Environment
) {
    private val client = clientFactory.create()

    suspend fun getContacts(): List<Contact> =
        client
            .runGet<BaseResponse<List<ContactsResponse>>>(
                path = "contacts"
            )
            .requireData()
            .let(contactsMapper::responseToContactsList)

    suspend fun sendContacts(contacts: List<Contact>) {
        return client
            .runPost<SendContactsRequest, BaseResponse<Unit>>(
                path = "contacts",
                body = SendContactsRequest(contactsMapper.contactListToRequest(contacts))
            )
            .requireData()
    }

    suspend fun deleteContacts(contactIds: List<Long>) {
        val body = DeleteContactsRequest(
            contacts = contactIds
        )
        return client.runDelete<DeleteContactsRequest, BaseResponse<Unit>>(
            path = "contacts",
            body = body
        ).requireData()
    }

    suspend fun updateContactName(name: String, phone: String) {
        return client
            .runPost<UpdateContactNameRequest, BaseResponse<Unit>>(
                path = "contacts/change-name",
                body = UpdateContactNameRequest(
                    name = name,
                    phone = phone
                )
            )
            .requireData()
    }

}
