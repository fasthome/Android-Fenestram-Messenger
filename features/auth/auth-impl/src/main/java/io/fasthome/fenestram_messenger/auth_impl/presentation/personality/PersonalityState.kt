package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.net.Uri
import io.fasthome.fenestram_messenger.util.PrintableText

data class PersonalityState(
    val fieldsData: List<Field>,
    val avatar: Uri?
){

    data class Field(
        val key : PersonalityFragment.EditTextKey,
        val text : PrintableText,
        val visibility: Boolean
    )
}


