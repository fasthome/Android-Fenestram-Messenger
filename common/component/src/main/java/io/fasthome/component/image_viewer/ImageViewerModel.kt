package io.fasthome.component.image_viewer

import android.os.Parcelable
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageViewerModel(
    val imageUrl: String?,
    val imageContent: Content?,
    val imageGallery: GalleryImage? = null
) : Parcelable