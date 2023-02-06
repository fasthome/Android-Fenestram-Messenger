package io.fasthome.component.imageViewer

import androidx.lifecycle.viewModelScope
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl.Companion.IMAGES_COUNT_MEDIUM_PAGE
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class ImageViewerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ImageViewerContract.ImageViewerParams,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<ImageViewerState, ImageViewerEvent>(
    router, requestParams
) {

    private var imagesFromGallery: List<ImageViewerModel> = emptyList()

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

    fun loadMoreImages(firstVisiblePos: Int) {
        val cursorPosition = imagesFromGallery.getOrNull(firstVisiblePos)?.imageGallery?.cursorPosition
        if (cursorPosition != null) {
            when (firstVisiblePos) {
                0 -> {
                    loadBeforeImages(cursorPosition)
                }
                imagesFromGallery.lastIndex -> {
                    loadAfterImages(cursorPosition)
                }
            }
        }
    }
    fun getImageFirstStart(galleryImage: GalleryImage) {
        loadBeforeImages(galleryImage.cursorPosition - 1, true)
        loadAfterImages(galleryImage.cursorPosition)
    }

    private fun loadAfterImages(cursorPosition: Int) {
        viewModelScope.launch {
            galleryRepository.getGalleryImagesAfter(cursorPosition, IMAGES_COUNT_MEDIUM_PAGE)
                .onSuccess { imagesGallery ->
                    imagesFromGallery = imagesFromGallery + imagesGallery.map { ImageViewerModel(null, null, it) }
                }
            sendEvent(ImageViewerEvent.GalleryImagesEvent(imagesFromGallery))
        }
    }
    private fun loadBeforeImages(cursorPosition: Int, needScrollAfterSet: Boolean = false) {
        viewModelScope.launch {
            galleryRepository.getGalleryImagesBefore(cursorPosition, IMAGES_COUNT_MEDIUM_PAGE)
                .onSuccess { imagesGallery ->
                    imagesFromGallery = imagesGallery.map { ImageViewerModel(null, null, it) } + imagesFromGallery
                }
            var scrollToPosition:Int? = null
                if(needScrollAfterSet) {
                currentViewState.imagesViewerModel.firstOrNull()?.imageGallery?.cursorPosition?.let { pos ->
                    scrollToPosition = imagesFromGallery.indexOfFirst { it.imageGallery?.cursorPosition == pos }
                }
            }
            sendEvent(ImageViewerEvent.GalleryImagesEvent(
                imagesFromGallery,
                cursorToScrollPos = scrollToPosition
            ))
        }
    }

    fun onDeleteImage() {
        exitWithResult(
            ImageViewerContract.createResult(
                ImageViewerContract.Result.Delete(
                    messageId = currentViewState.messageId ?: return
                )
            )
        )
    }

    fun onForwardImage() {
        exitWithResult(
            ImageViewerContract.createResult(
                ImageViewerContract.Result.Forward(
                    messageId = currentViewState.messageId ?: return,
                    username = currentViewState.username ?: return
                )
            )
        )
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }
}