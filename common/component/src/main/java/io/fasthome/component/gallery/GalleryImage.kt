package io.fasthome.component.gallery

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryImage(
    val uri: Uri,
    val cursorPosition: Int,
    var isChecked: Boolean = false
) : Parcelable {
}