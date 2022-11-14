package io.fasthome.component.imageViewer

import android.graphics.Bitmap

data class ImageViewerState(
    val imageUrl: String?, val imageBitmap: Bitmap?,
    val messageId: Long? = null,
    val chatId: Long? = null,
    val canDelete: Boolean,
    val canForward: Boolean
)