package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText

data class RecentImagesViewItem(
    val image: Content,
    val hasMoreImages: Boolean,
    val moreImagesCount: PrintableText
)