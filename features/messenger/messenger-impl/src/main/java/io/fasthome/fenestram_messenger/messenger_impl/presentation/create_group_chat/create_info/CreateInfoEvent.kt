package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationEvent

sealed interface CreateInfoEvent {
    object ShowSelectFromDialog : CreateInfoEvent
}