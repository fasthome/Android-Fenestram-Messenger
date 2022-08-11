package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.graphics.Bitmap
import android.net.Uri
import io.fasthome.fenestram_messenger.util.PrintableText
import java.io.File

data class PersonalityState(
    val fieldsData: List<Field>,
    val avatarBitmap : Bitmap?,
    val originalProfileImageFile : File?
){

    data class Field(
        val key : PersonalityFragment.EditTextKey,
        val text : PrintableText,
        val visibility: Boolean
    )
}


