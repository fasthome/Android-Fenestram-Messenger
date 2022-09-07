package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.util.PrintableText
import java.time.LocalDateTime
import java.time.ZonedDateTime

sealed interface ConversationViewItem {

    val content: PrintableText
    val time: PrintableText
    val date: ZonedDateTime?

    data class Self(
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        val sendStatus: Boolean
    ) : ConversationViewItem

    data class Receive(
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        val sendStatus: Boolean
    ) : ConversationViewItem

    data class Group(
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        val sendStatus: Boolean,
        val userName: PrintableText,
        val avatar: String
    ) : ConversationViewItem

    data class System(
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?
    ) : ConversationViewItem
}
