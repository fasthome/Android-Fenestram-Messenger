package io.fasthome.component.imageViewer

import android.graphics.Bitmap
import io.fasthome.fenestram_messenger.util.PrintableText

data class ImageViewerState(
    val imageUrl: String?, val imageBitmap: Bitmap?,
    val messageId: Long? = null,
    val chatId: Long? = null,
    val canDelete: Boolean,
    val canForward: Boolean,
    val username: PrintableText? = null
)