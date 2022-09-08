package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.util.PrintableText

sealed class ContactsViewItem {

    abstract val avatar: Any
    abstract val name: PrintableText

    class Local(
        override val avatar: Int,
        override val name: PrintableText
    ) : ContactsViewItem()

    class Api(
        val userId: Long,
        override val avatar: String,
        override val name: PrintableText,
        val phone: PrintableText
    ) : ContactsViewItem()

    class Header(
        override val avatar: Any,
        override val name: PrintableText
    ) : ContactsViewItem()
}