package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model

import androidx.annotation.DrawableRes

data class RecentImagesViewItem(
    @DrawableRes val image: Int,
    val imageCount: Int,
    val showAll: Boolean
)