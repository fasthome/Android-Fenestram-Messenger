package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model

import io.fasthome.fenestram_messenger.util.PrintableText

data class ContactsViewItem(
    val userId: Long?,
    val avatar: String,
    val name: PrintableText
)