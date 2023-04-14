package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

sealed interface GroupGuestEvent{
    class CopyTextEvent(val link: String): GroupGuestEvent
    class Loading(val isLoading : Boolean) : GroupGuestEvent
}