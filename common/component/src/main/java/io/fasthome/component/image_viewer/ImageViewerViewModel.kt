package io.fasthome.component.image_viewer

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.fasthome.component.R
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl.Companion.IMAGES_COUNT_MEDIUM_PAGE
import io.fasthome.component.image_viewer.ImageViewerMapper.toImageViewerModel
import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.fenestram_messenger.data.file.DownloadImageManager
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.uikit.paging.ListWithTotal
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import io.fasthome.fenestram_messenger.uikit.paging.totalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ImageViewerViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ImageViewerContract.ImageViewerParams,
    private val galleryRepository: GalleryRepository,
    private val loadDataHelper: PagingDataViewModelHelper,
    private val downloadImageManager: DownloadImageManager
) : BaseViewModel<ImageViewerState, ImageViewerEvent>(
    router, requestParams
) {

    var scrollCursorPosition: Int? = null
    override fun createInitialState(): ImageViewerState {
        val fromConversationParams = params as? ImageViewerContract.ImageViewerParams.MessageImageParams
        val someImagesConversationParams = params as? ImageViewerContract.ImageViewerParams.ImagesParams
        return ImageViewerState(
            messageId = fromConversationParams?.messageId,
            canDelete = fromConversationParams?.canDelete ?: false,
            canForward = fromConversationParams != null,
            canDownload = params.imageViewerModel.firstOrNull()?.imageGallery == null,
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
                        galleryRepository.getGalleryImages(pageNumber, pageSize).onSuccess { images ->
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
                        scrollCursorPosition = currentViewState.currPhotoPosition
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

    fun onForwardImage(imagePos: Int) {
        exitWithResult(
            ImageViewerContract.createResult(
                ImageViewerContract.Result.ForwardImage(
                    messageId = currentViewState.messageId ?: return,
                    imageUrl = currentViewState.imagesViewerModel.getOrNull(imagePos)?.imageUrl ?: return,
                    username = currentViewState.username ?: return
                )
            )
        )
    }

    fun onDownloadImage(imagePos: Int) {
        viewModelScope.launch {
            val imageUrl = currentViewState.imagesViewerModel.getOrNull(imagePos)?.imageUrl ?: run {
                showPopup(PrintableText.StringResource(R.string.image_viewer_download_error))
                return@launch
            }

            sendEvent(ImageViewerEvent.ToggleProgressEvent(isProgressVisible = true))
            when (downloadImageManager.downloadImage(imageUrl)) {
                is CallResult.Success -> {}
                is CallResult.Error -> {
                    showPopup(PrintableText.StringResource(R.string.image_viewer_download_error))
                }
            }
            sendEvent(ImageViewerEvent.ToggleProgressEvent(isProgressVisible = false))
        }
    }

    fun updateCounter(curPhotoPos: Int) {
        updateState { state -> state.copy(currPhotoPosition = curPhotoPos) }
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }
}