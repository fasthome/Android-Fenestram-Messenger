package io.fasthome.component.imageViewer

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ImageViewerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ImageViewerContract.ImageViewerParams,
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
            imagesViewerModel = params.imageViewerModel
        )
    }

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