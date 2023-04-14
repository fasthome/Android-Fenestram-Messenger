package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.mapper

import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UrlLoadableContent

object ImagesMapper {

    fun toImageViewerItem(fileItem: FileItem.Image) = when (fileItem.content) {
        is UrlLoadableContent -> ImageViewerModel(
            imageUrl = fileItem.content.url,
            imageContent = null,
            imageGallery = null
        )
        else -> ImageViewerModel(
            imageUrl = null,
            imageContent = fileItem.content,
            imageGallery = null
        )
    }

}