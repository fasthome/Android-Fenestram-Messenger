package io.fasthome.component.profile_verification

import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText
import java.io.File

data class ProfileVerificationState(
    val userAvatar: String? = null,
    val avatarContent: Content? = null,
    val imageFile: File? = null,
    val userName: PrintableText?,
    val email: PrintableText?,
    val speciality: PrintableText?,
    val birthday: PrintableText?,
    val department: PrintableText?
)