package io.fasthome.component.image_viewer

import io.fasthome.component.gallery.GalleryImage
import io.fasthome.component.image_viewer.model.ImageViewerModel

object ImageViewerMapper {
    fun GalleryImage.toImageViewerModel() = ImageViewerModel(null,null,this)
}