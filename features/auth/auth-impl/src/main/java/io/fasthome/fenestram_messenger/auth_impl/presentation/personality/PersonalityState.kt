package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.net.Uri

data class PersonalityState(
    val key: PersonalityFragment.EditTextKey?,
    val visibility: Boolean,
    val avatar: Uri?
)


