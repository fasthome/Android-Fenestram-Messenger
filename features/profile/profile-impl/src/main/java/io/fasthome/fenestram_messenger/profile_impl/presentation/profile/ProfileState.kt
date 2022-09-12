/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.graphics.Bitmap
import io.fasthome.fenestram_messenger.util.PrintableText
import java.io.File

data class ProfileState(
    val fieldsData: List<Field>,
    val avatarUrl: String?,
    val avatarBitmap: Bitmap?,
    val originalProfileImageFile: File?,
    val isEdit: Boolean,
    val isLoad: Boolean
) {

    data class Field(
        val key: EditTextKey,
        val text: PrintableText,
        val visibility: Boolean
    )

    enum class EditTextKey {
        UsernameKey,
        NicknameKey,
        BirthdateKey,
        MailKey
    }
}


