package io.fasthome.component.imageViewer

interface ImageViewerEvent {
    class GalleryImagesEvent(val galleryImages: List<ImageViewerModel>, val cursorToScrollPos: Int? = null) : ImageViewerEvent
}