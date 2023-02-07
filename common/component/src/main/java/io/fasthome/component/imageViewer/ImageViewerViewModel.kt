package io.fasthome.component.imageViewer

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl.Companion.IMAGES_COUNT_MEDIUM_PAGE
import io.fasthome.component.imageViewer.ImageViewerMapper.toImageViewerModel
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.uikit.paging.ListWithTotal
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import io.fasthome.fenestram_messenger.uikit.paging.totalPagingSource
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.Flow

class ImageViewerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ImageViewerContract.ImageViewerParams,
    private val galleryRepository: GalleryRepository,
    private val loadDataHelper: PagingDataViewModelHelper,
) : BaseViewModel<ImageViewerState, ImageViewerEvent>(
    router, requestParams
) {

    var scrollCursorPosition: Int? = null
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

    fun fetchImages(): Flow<PagingData<ImageViewerModel>> {
        val imageGallery = currentViewState.imagesViewerModel.firstOrNull()?.imageGallery
        var index = 0
        val items = loadDataHelper.getDataFlow(
            getItems = {
                totalPagingSource(IMAGES_COUNT_MEDIUM_PAGE) { pageNumber, pageSize ->
                    if (imageGallery != null) {
                        galleryRepository.getGalleryImages(pageNumber, pageSize)
                            .onSuccess { images ->
                                return@totalPagingSource ListWithTotal(images, pageSize)
                            }
                    } else {
                        if (pageNumber == 0 && currentViewState.imagesViewerModel.isNotEmpty()) {
                            return@totalPagingSource ListWithTotal(
                                currentViewState.imagesViewerModel.filterIsInstance<ImageViewerModel>(),
                                currentViewState.imagesViewerModel.size
                            )
                        }
                    }
                    return@totalPagingSource ListWithTotal(emptyList(), pageSize)
                }
            },
            mapDataItem = {
                when (it) {
                    is GalleryImage -> {
                        if (imageGallery != null) {
                            if (it.cursorPosition == imageGallery.cursorPosition) {
                                scrollCursorPosition = index
                            }
                            index++
                        }
                        it.toImageViewerModel()
                    }
                    else -> {
                        it as ImageViewerModel
                    }
                }
            },
            getItemId = { if (it is GalleryImage) it.cursorPosition.toLong() else null },
        ).cachedIn(viewModelScope)
        return items
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