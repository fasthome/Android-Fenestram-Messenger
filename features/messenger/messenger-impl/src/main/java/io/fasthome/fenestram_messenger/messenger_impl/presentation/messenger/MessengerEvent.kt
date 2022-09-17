package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

sealed interface MessengerEvent {
    class DeleteChatEvent(val id : Long) : MessengerEvent
}