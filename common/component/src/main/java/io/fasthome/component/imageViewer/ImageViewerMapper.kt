package io.fasthome.component.imageViewer

import io.fasthome.component.gallery.GalleryImage

object ImageViewerMapper {
    fun GalleryImage.toImageViewerModel() = ImageViewerModel(null,null,this)
}