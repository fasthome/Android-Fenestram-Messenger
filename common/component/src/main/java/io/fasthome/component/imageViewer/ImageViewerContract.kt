package io.fasthome.component.imageViewer

import android.graphics.Bitmap
import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.parcelize.Parcelize

object ImageViewerContract :
    NavigationContract<ImageViewerContract.ImageViewerParams, ImageViewerContract.Result>(
        ImageViewerDialog::class) {

    sealed class ImageViewerParams() : Parcelable {
        abstract val imageUrl: String?
        abstract val imageBitmap: Bitmap?

        @Parcelize
        data class ImageParams(
            override val imageUrl: String?,
            override val imageBitmap: Bitmap?,
        ) : ImageViewerParams()

        @Parcelize
        data class MessageImageParams(
            override val imageUrl: String?,
            override val imageBitmap: Bitmap?,
            val messageId: Long,
            val canDelete: Boolean,
            val username: PrintableText
        ) : ImageViewerParams()

    }

    sealed class Result : Parcelable {

        abstract val messageId: Long

        @Parcelize
        class Delete(
            override val messageId: Long,
        ) : Result()

        @Parcelize
        class Forward(
            override val messageId: Long,
            val username: PrintableText
        ) : Result()
    }
}