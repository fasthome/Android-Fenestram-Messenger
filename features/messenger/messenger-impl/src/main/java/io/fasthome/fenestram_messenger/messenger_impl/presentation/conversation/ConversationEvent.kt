package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation

import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem

sealed interface ConversationEvent {
    object OpenMenuEvent : ConversationEvent
    class ShowDeleteChatDialog(val id: Long) : ConversationEvent
    object MessageSent : ConversationEvent
    object InvalidateList : ConversationEvent
    object ShowSelectFromDialog : ConversationEvent
    class ShowPersonDetailDialog(val selectedPerson: PersonDetail) : ConversationEvent
    class ShowErrorSentDialog(val conversationViewItem: ConversationViewItem.Self) :
        ConversationEvent
    class ShowSelfMessageActionDialog(val conversationViewItem: ConversationViewItem.Self) : ConversationEvent
}