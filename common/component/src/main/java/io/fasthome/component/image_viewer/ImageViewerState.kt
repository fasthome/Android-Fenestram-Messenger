package io.fasthome.component.image_viewer

import io.fasthome.fenestram_messenger.util.PrintableText

data class ImageViewerState(
    val messageId: Long? = null,
    val chatId: Long? = null,
    val canDelete: Boolean,
    val canForward: Boolean,
    val username: PrintableText? = null,
    val currPhotoPosition: Int? = null,
    val imagesViewerModel: List<ImageViewerModel?> = emptyList(),
)