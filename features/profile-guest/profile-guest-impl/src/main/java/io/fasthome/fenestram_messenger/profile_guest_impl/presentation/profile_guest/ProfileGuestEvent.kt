package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

sealed interface ProfileGuestEvent {
    object DeleteChatEvent: ProfileGuestEvent
}