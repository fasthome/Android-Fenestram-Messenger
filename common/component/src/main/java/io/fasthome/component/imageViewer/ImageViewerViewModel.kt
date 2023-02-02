package io.fasthome.component.imageViewer

import io.fasthome.component.gallery.GalleryImage
import io.fasthome.component.gallery.GalleryOperations
import io.fasthome.component.gallery.GalleryOperationsImpl
import io.fasthome.component.gallery.GalleryOperationsImpl.Companion.IMAGES_COUNT_MEDIUM_PAGE
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ImageViewerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ImageViewerContract.ImageViewerParams,
    private val galleryOperations: GalleryOperations,
) : BaseViewModel<ImageViewerState, ImageViewerEvent>(
    router, requestParams
) {
    override fun createInitialState(): ImageViewerState {
        val fromConversationParams =
            params as? ImageViewerContract.ImageViewerParams.MessageImageParams
        val someImagesConversationParams =
            params as? ImageViewerContract.ImageViewerParams.ImagesParams
        return ImageViewerState(
            messageId = fromConversationParams?.messageId,
            canDelete = fromConversationParams?.canDelete ?: false,
            canForward = fromConversationParams != null,
            username = fromConversationParams?.username,
            currPhotoPosition = someImagesConversationParams?.currentImagePosition,
            imagesViewerModel = params.imageViewerModel,
        )
    }

    fun getImageFirstStart(galleryImage: GalleryImage): List<ImageViewerModel> {
        val afterImages = loadAfterImages(galleryImage.cursorPosition)
        val beforeImages = loadBeforeImages(galleryImage.cursorPosition-1)
        return beforeImages + afterImages
    }

    fun loadAfterImages(cursorPosition: Int) = galleryOperations.getGalleryImagesAfter(cursorPosition, IMAGES_COUNT_MEDIUM_PAGE).map { ImageViewerModel(null,null,it) }

    fun loadBeforeImages(cursorPosition: Int) = galleryOperations.getGalleryImagesBefore(cursorPosition,IMAGES_COUNT_MEDIUM_PAGE).map { ImageViewerModel(null,null,it) }

    fun onDeleteImage() {
        exitWithResult(ImageViewerContract.createResult(ImageViewerContract.Result.Delete(
            messageId = currentViewState.messageId ?: return
        )))
    }

    fun onForwardImage() {
        exitWithResult(ImageViewerContract.createResult(ImageViewerContract.Result.Forward(
            messageId = currentViewState.messageId ?: return,
            username = currentViewState.username ?: return
        )))
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }
}