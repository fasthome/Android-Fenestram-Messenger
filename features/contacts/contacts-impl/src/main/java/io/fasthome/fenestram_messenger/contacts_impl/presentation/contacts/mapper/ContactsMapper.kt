package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

object ContactsMapper {

    fun contactToViewItem(contact: Contact): ContactsViewItem {
        return ContactsViewItem(
            id = contact.userId,
            avatar = 0,
            name = PrintableText.Raw(contact.userName ?: contact.phone),
            newMessageVisibility = 0
        )
    }

}