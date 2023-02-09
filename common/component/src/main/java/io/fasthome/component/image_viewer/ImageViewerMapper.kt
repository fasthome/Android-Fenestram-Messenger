package io.fasthome.component.image_viewer

import io.fasthome.component.gallery.GalleryImage

object ImageViewerMapper {
    fun GalleryImage.toImageViewerModel() = ImageViewerModel(null,null,this)
}