package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

sealed interface ProfileGuestEvent {
    class DeleteChatEvent(val id: Long) : ProfileGuestEvent
}