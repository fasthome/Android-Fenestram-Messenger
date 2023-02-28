package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.util.PrintableText

sealed interface ProfileGuestEvent {
    class DeleteChatEvent(val id: Long) : ProfileGuestEvent
    class Loading(val isLoading: Boolean) : ProfileGuestEvent
    class CopyText(val text: String, val toastMessage: PrintableText) : ProfileGuestEvent

    object OpenCamera : ProfileGuestEvent
    object OpenImagePicker : ProfileGuestEvent
}