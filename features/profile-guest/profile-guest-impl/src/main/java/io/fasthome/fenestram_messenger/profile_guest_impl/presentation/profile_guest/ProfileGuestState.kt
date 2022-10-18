package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import android.graphics.Bitmap
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.EditTextStatus
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import java.io.File

data class ProfileGuestState(
    val userName: PrintableText,
    val userNickname: PrintableText,
    val userPhone: PrintableText,
    val userAvatar: String,
    val recentFiles: List<RecentFilesViewItem>,
    val recentImages: List<RecentImagesViewItem>,
    val isGroup: Boolean,
    val editMode: Boolean,
    val avatarBitmap: Bitmap?,
    val chatImageFile: File?,
    val participantsQuantity: Int,
    val profileGuestStatus: EditTextStatus
) {
}