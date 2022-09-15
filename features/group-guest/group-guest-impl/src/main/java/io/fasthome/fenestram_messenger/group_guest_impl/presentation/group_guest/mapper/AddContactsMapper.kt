/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.model.AddContactViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

fun mapToContactViewItem(contact: Contact) =
    AddContactViewItem(
        userId = contact.userId,
        userName = PrintableText.Raw(contact.userName ?: contact.phone)
    )