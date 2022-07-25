package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem

data class ConversationState(
    val messages: List<ConversationViewItem>
)