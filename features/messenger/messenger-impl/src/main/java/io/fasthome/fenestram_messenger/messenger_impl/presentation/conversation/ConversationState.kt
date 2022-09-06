package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

data class ConversationState(
    val messages: List<ConversationViewItem>,
    val userName: PrintableText,
    val avatar : String,
    val userOnline: Boolean,
    val isChatEmpty: Boolean
)