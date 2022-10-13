package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.util.PrintableText

sealed interface ProfileGuestEvent {
    class DeleteChatEvent(val id: Long) : ProfileGuestEvent
    class SetProfileName(val name: PrintableText) : ProfileGuestEvent
}