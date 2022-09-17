package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem

sealed interface ConversationEvent {
    object OpenMenuEvent: ConversationEvent
    class ShowDeleteChatDialog(val id : Long): ConversationEvent
    object MessageSent : ConversationEvent
    object InvalidateList : ConversationEvent
    object ShowSelectFromDialog : ConversationEvent
    class ShowErrorSentDialog(val conversationViewItem: ConversationViewItem.Self) : ConversationEvent
}