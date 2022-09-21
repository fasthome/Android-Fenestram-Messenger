package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFile
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

data class ConversationState(
    val messages: Map<String, ConversationViewItem>,
    val userName: PrintableText,
    val avatar: String,
    val userOnline: Boolean,
    val isChatEmpty: Boolean,
    val attachedFiles: List<AttachedFile.Image>
)