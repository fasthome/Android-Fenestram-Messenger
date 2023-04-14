package io.fasthome.fenestram_messenger.camera_api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CapturedItem(
    val id: String,
) : Parcelable
