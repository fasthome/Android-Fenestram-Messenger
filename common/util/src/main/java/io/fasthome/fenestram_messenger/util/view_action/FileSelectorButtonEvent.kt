package io.fasthome.fenestram_messenger.util.view_action

sealed interface FileSelectorButtonEvent {
    class AttachCountEvent(val count: Int, val isHaveChanges: Boolean = false, val oneSelect: Boolean): FileSelectorButtonEvent
    object AttachEvent: FileSelectorButtonEvent

    object IgnoreRouterExit: FileSelectorButtonEvent
}