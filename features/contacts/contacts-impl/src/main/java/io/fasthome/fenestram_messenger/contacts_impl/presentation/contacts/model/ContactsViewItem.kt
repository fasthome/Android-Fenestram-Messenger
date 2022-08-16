package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.util.PrintableText

data class ContactsViewItem(
    val id: Long?,
    @DrawableRes val avatar: Int,
    val name: PrintableText,
    val newMessageVisibility: Int,
)