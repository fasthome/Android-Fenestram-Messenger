package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CapturedItem(
    val id: String,
) : Parcelable
