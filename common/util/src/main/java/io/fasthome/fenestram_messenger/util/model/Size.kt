package io.fasthome.fenestram_messenger.util.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Size(
    val width: Int,
    val height: Int,
) : Parcelable {
    fun toAndroidSize() = android.util.Size(width, height)
}