package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText
import java.io.File

data class PersonalityState(
    val fieldsData: List<Field>,
    val avatarContent : Content?,
    val originalProfileImageFile : File?,
    val profileImageUrl : String?,
    val readyEnabled : Boolean,
    val label : PrintableText
){

    data class Field(
        val key : PersonalityFragment.EditTextKey,
        val text : PrintableText,
        val visibility: Boolean
    )
}


