package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_edit

import android.graphics.Bitmap
import java.io.File

data class ProfileGuestEditState(
    val chatName: String,
    val avatarUrl: String,
    val avatarBitmap: Bitmap?,
    val chatImageFile: File?,
    val participantsQuantity: Int
)