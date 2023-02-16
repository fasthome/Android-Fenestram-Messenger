package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model

import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UrlLoadableContent
import io.fasthome.fenestram_messenger.util.PrintableText

data class RecentImagesViewItem(
    val image: UrlLoadableContent,
    val hasMoreImages: Boolean,
    val moreImagesCount: PrintableText
)