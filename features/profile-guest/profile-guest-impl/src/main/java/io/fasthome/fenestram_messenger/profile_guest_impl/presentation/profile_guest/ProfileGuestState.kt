package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

data class ProfileGuestState(
    val userName : PrintableText,
    val userNickname : PrintableText,
    val recentFiles: List<RecentFilesViewItem>,
    val recentImages: List<RecentImagesViewItem>
)