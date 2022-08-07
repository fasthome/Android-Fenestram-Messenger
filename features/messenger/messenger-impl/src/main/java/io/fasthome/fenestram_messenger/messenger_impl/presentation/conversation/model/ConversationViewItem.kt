package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.util.PrintableText

sealed interface ConversationViewItem {

    abstract val content: PrintableText
    abstract val time: String
    abstract val sendStatus: Boolean

    data class Self(
        override val content: PrintableText, override val time: String, override val sendStatus: Boolean
    ) : ConversationViewItem

    data class Receive(
        override val content: PrintableText, override val time: String, override val sendStatus: Boolean
    ) : ConversationViewItem
}
