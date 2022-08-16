package io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsRequest
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsResponse
import java.text.Collator
import java.util.*
import java.util.Collections.sort

object ContactsMapper {

    fun contactToRequest(contact: Contact): ContactsRequest {
        val phone = contact.phone
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace("+", "")

        return ContactsRequest(
            name = contact.userName,
            phone = phone
        )
    }

    fun responseToContactsList(contactsResponse: List<ContactsResponse>): List<Contact> {
        val contacts = contactsResponse.map {
            Contact(
                phone = it.phone,
                userName = it.name,
                userId = it.user?.id
            )
        }

        val locale = Locale("ru", "RU")
        val collator: Collator = Collator.getInstance(locale)

        sort(contacts) { o1: Contact, o2: Contact ->
            collator.compare(o1.userName, o2.userName)
        }

        return contacts.sortedBy {
            it.userId != null
        }
    }

}