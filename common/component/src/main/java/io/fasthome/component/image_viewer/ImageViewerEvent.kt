package io.fasthome.component.image_viewer

sealed interface ImageViewerEvent {
    class ToggleProgressEvent(val isProgressVisible: Boolean) : ImageViewerEvent
}