package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem

data class ProfileGuestState(
    val recentFiles: List<RecentFilesViewItem>,
    val recentImages: List<RecentImagesViewItem>
)