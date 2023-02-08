package io.fasthome.component.image_viewer

import android.os.Parcelable
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContract
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.parcelize.Parcelize

object ImageViewerContract :
    NavigationContract<ImageViewerContract.ImageViewerParams, ImageViewerContract.Result>(
        ImageViewerDialog::class) {

    sealed class ImageViewerParams() : Parcelable {
        abstract val imageViewerModel: List<ImageViewerModel>

        @Parcelize
        data class ImageParams(
            val imageModel: ImageViewerModel,
        ) : ImageViewerParams() {
            override val imageViewerModel: List<ImageViewerModel> = listOf(imageModel)
        }

        @Parcelize
        data class ImagesParams(
            override val imageViewerModel: List<ImageViewerModel>,
            val currentImagePosition: Int,
        ) : ImageViewerParams()

        @Parcelize
        data class MessageImageParams(
            override val imageViewerModel: List<ImageViewerModel>,
            val messageId: Long,
            val canDelete: Boolean,
            val username: PrintableText,
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
            val username: PrintableText,
        ) : Result()
    }
}