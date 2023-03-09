package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model

import io.fasthome.fenestram_messenger.util.PrintableText

sealed interface DepartmentViewItem {

    data class Division(
        val title: PrintableText,
        val employee: List<ContactsViewItem>,
        var textColor: Int? = null
    ): DepartmentViewItem {
        var isOpen = false
    }

    data class Header(
        val header: PrintableText,
    ): DepartmentViewItem

}