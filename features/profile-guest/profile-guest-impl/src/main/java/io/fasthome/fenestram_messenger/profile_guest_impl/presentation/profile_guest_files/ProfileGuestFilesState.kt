package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files

import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.FilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.model.AllFilesViewItem

data class ProfileGuestFilesState(
    val files: List<AllFilesViewItem>
)