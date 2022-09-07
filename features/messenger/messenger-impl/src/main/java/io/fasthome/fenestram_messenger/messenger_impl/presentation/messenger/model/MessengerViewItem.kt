package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.util.PrintableText

data class MessengerViewItem(
    val id: Long,
    @DrawableRes val avatar: Int,
    val name: PrintableText,
    val newMessages: Int,
    val time : PrintableText,
    val lastMessage: PrintableText,
    val profileImageUrl : String?
)