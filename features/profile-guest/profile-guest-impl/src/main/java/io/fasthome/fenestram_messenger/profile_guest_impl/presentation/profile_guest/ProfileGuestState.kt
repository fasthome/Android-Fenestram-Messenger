package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.FilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.PhotosViewItem

data class ProfileGuestState(
    val files: List<FilesViewItem>,
    val photos: List<PhotosViewItem>
)