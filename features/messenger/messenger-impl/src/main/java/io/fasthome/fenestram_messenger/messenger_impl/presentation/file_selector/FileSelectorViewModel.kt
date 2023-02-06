/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl
import io.fasthome.component.imageViewer.ImageViewerContract
import io.fasthome.component.imageViewer.ImageViewerModel
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.paging.ListWithTotal
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import io.fasthome.fenestram_messenger.uikit.paging.totalPagingSource
import io.fasthome.fenestram_messenger.util.view_action.BottomViewAction
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.fenestram_messenger.util.view_action.FileSelectorButtonEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FileSelectorViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val galleryRepository: GalleryRepository,
    private val bottomViewAction: BottomViewAction<FileSelectorButtonEvent>,
    private val loadDataHelper: PagingDataViewModelHelper,
) : BaseViewModel<FileSelectorState, FileSelectorEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract) { }
    private var attachedImages: List<Content> = emptyList()

    init {
        viewModelScope.launch {
            galleryRepository.getGalleryImages(0, GalleryRepositoryImpl.IMAGES_COUNT_ON_PAGE)
                .onSuccess { images ->
                    updateState { it.copy(images = images) }
                }

            bottomViewAction.receiveActionsToFragment().collectLatest {
                if (it is FileSelectorButtonEvent.AttachEvent) {
                    exitWithResult(
                        FileSelectorNavigationContract.createResult(
                            FileSelectorNavigationContract.Result.Attach(images = attachedImages)
                        )
                    )
                }
            }
        }
    }

    fun fetchImages(): Flow<PagingData<GalleryImage>> {
        val items = loadDataHelper.getDataFlow(
            getItems = {
                totalPagingSource(GalleryRepositoryImpl.IMAGES_COUNT_ON_PAGE) { pageNumber, pageSize ->
                    galleryRepository.getGalleryImages(pageNumber, pageSize).onSuccess { images ->
                        return@totalPagingSource ListWithTotal(images, pageSize)
                    }
                    return@totalPagingSource ListWithTotal(emptyList(), pageSize)
                }
            },
            getCachedSelectedId = { null },
            mapDataItem = { it },
            getItemId = { it.cursorPosition.toLong() },
            getItem = { null },
        ).cachedIn(viewModelScope)
        return items
    }

    fun onAttachFiles(selectedList: List<Content>) {
        bottomViewAction.sendActionToBottomView(FileSelectorButtonEvent.AttachCountEvent(selectedList.size))
        attachedImages = selectedList
    }

    fun onImageClicked(galleryImage: GalleryImage) {
        val params = ImageViewerContract.ImageViewerParams.ImageParams(
            imageModel = ImageViewerModel(
                null, null, imageGallery = galleryImage
            )
        )
        imageViewerLauncher.launch(params)
    }

    override fun createInitialState(): FileSelectorState = FileSelectorState(
        images = listOf()
    )

    fun fromGalleryClicked() {
        bottomViewAction.sendActionToBottomView(FileSelectorButtonEvent.SheetCloseEvent)
       // exitWithResult(FileSelectorNavigationContract.createResult(FileSelectorNavigationContract.Result.OpenGallery))
    }

    fun fromCameraClicked() {
        exitWithResult(FileSelectorNavigationContract.createResult(FileSelectorNavigationContract.Result.OpenCamera))
    }

    fun attachFileClicked() {
        exitWithResult(FileSelectorNavigationContract.createResult(FileSelectorNavigationContract.Result.OpenFiles))
    }

}