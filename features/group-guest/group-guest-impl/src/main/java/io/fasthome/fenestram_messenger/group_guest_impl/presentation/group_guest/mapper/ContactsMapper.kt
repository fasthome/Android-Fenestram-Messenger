/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

fun mapToContactViewItem(contact: Contact) =
    ContactViewItem(
        userId = contact.userId,
        userName = PrintableText.Raw(contact.userName ?: contact.phone)
    )