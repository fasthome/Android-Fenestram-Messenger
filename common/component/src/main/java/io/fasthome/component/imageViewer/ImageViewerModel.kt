package io.fasthome.component.imageViewer

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageViewerModel(
    val imageUrl: String?,
    val imageBitmap: Bitmap?,
) : Parcelable