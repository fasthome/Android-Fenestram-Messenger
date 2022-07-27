package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model

import androidx.annotation.DrawableRes

data class MessengerViewItem(
    val id: Long,
    @DrawableRes val avatar: Int,
    val name: String,
    val newMessages: Int
)