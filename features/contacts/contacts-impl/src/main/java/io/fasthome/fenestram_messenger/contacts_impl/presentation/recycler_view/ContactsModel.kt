package io.fasthome.fenestram_messenger.contacts_impl.presentation.recycler_view

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.contacts_impl.R

data class ContactsModel(
    val id: Long,
    @DrawableRes val avatar: Int,
    val name: String,
    val newMessage: Boolean
)

var contactsList = mutableListOf(
    ContactsModel(
        id = 1,
        name = "John",
        avatar = R.drawable.bg_account_circle,
        newMessage = false
    ),
    ContactsModel(
        id = 2,
        name = "Maria",
        avatar = R.drawable.bg_account_circle,
        newMessage = true
    ),
    ContactsModel(
        id = 3,
        name = "Henry",
        avatar = R.drawable.bg_account_circle,
        newMessage = false
    ),
)