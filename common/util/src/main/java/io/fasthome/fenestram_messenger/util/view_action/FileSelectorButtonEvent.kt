package io.fasthome.fenestram_messenger.util.view_action

sealed interface FileSelectorButtonEvent {
    class AttachCountEvent(val count: Int): FileSelectorButtonEvent
    object AttachEvent: FileSelectorButtonEvent
}