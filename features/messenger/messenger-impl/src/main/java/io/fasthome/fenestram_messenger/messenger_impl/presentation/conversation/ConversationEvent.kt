package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

sealed interface ConversationEvent {
    object OpenMenuEvent: ConversationEvent
    class ShowDialog(val id : Long): ConversationEvent
    object MessageSent : ConversationEvent
    object InvalidateList : ConversationEvent
    object ShowSelectFromDialog : ConversationEvent

}