package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem

sealed interface InputMessageMode {
    data class Default(val inputText: String? = null) : InputMessageMode
    data class Edit(val messageToEdit: ConversationViewItem.Self) : InputMessageMode
    data class Reply(val messageToReply: ConversationViewItem) : InputMessageMode
}