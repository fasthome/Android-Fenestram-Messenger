package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.util.PrintableText
import java.time.LocalDateTime

sealed interface ConversationViewItem {

    val content: PrintableText
    val time: PrintableText
    val sendStatus: Boolean

    data class Self(
        override val content: PrintableText,
        override val time: PrintableText,
        override val sendStatus: Boolean
    ) : ConversationViewItem

    data class Receive(
        override val content: PrintableText,
        override val time: PrintableText,
        override val sendStatus: Boolean
    ) : ConversationViewItem

    data class Group(
        override val content: PrintableText,
        override val time: PrintableText,
        override val sendStatus: Boolean,
        val userName: PrintableText,
        val avatar: String
    ) : ConversationViewItem
}
