package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model

import androidx.annotation.DrawableRes

data class ContactsViewItem(
    val id: Long,
    @DrawableRes val avatar: Int,
    val name: String,
    val newMessageVisibility: Int,
)