package io.fasthome.fenestram_messenger.profile_impl.presentation.profile.model

import androidx.annotation.DrawableRes

data class ProfileViewItem(
    val id: Long,
    @DrawableRes val avatar: Int,
    val name: String,
    val newMessageVisibility: Int,
)