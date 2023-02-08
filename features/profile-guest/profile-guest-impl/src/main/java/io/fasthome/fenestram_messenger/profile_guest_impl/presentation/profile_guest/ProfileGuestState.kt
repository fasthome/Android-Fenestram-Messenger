package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.EditTextStatus
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText
import java.io.File

data class ProfileGuestState(
    val userName: PrintableText,
    val userNickname: PrintableText,
    val userPhone: PrintableText,
    val userAvatar: String,

    val recentImages: List<RecentImagesViewItem>,
    val recentImagesCount : PrintableText,

    val recentFiles: List<RecentFilesViewItem>,
    val recentFilesCount : PrintableText,

    val isGroup: Boolean,
    val editMode: Boolean,
    val avatarContent: Content?,
    val chatImageFile: File?,
    val participantsQuantity: Int,
    val profileGuestStatus: EditTextStatus
) {
}