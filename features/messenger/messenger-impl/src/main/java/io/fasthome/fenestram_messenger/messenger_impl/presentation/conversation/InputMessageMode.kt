package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem

sealed interface InputMessageMode {
    object Default : InputMessageMode
    data class Edit(val messageToEdit: ConversationViewItem.Self.Text) : InputMessageMode
    data class Reply(val messageToReply: ConversationViewItem) : InputMessageMode
}