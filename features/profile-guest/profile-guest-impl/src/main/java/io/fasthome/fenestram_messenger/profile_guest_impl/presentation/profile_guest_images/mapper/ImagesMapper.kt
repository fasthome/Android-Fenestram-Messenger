package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.mapper

import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem

object ImagesMapper {

    fun toImageViewerItem(fileItem: FileItem.Image) = ImageViewerModel(
        imageUrl = null,
        imageContent = fileItem.content,
        imageGallery = null
    )

}