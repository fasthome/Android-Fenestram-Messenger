/**
 * Created by Dmitry Popov on 02.02.2023.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl
import io.fasthome.component.image_viewer.ImageViewerContract
import io.fasthome.component.image_viewer.ImageViewerModel
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFileMapper.toListUri
import io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector.FileSelectorMapper.toContents
import io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector.FileSelectorMapper.toFileSelectorViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector.FileSelectorMapper.toGalleryImage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector.FileSelectorMapper.toViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.contract.CreateResultInterface
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.uikit.paging.ListWithTotal
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper
import io.fasthome.fenestram_messenger.uikit.paging.totalPagingSource
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.fenestram_messenger.util.view_action.BottomViewAction
import io.fasthome.fenestram_messenger.util.view_action.FileSelectorButtonEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FileSelectorViewModel(
    router: ContractRouter,
    private val requestParams: RequestParams,
    private val galleryRepository: GalleryRepository,
    private val params: FileSelectorNavigationContract.Params,
    private val bottomViewAction: BottomViewAction<FileSelectorButtonEvent>,
    private val loadDataHelper: PagingDataViewModelHelper,
) : BaseViewModel<FileSelectorState, FileSelectorEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract) { }
    private var attachedImages = mutableListOf<FileSelectorViewItem>()

    init {
        attachedImages.addAll(params.selectedImages.toFileSelectorViewItem())
        sendImagesCount()
        viewModelScope.launch {
            bottomViewAction.receiveActionsToFragment().collectLatest {
                when (it) {
                    is FileSelectorButtonEvent.AttachEvent -> {
                        exitWithResult(
                            FileSelectorNavigationContract.createResult(
                                FileSelectorNavigationContract.Result.Attach(images = attachedImages.toContents())
                            )
                        )
                    }
                }
            }
        }
    }

    fun fetchImages(): Flow<PagingData<FileSelectorViewItem>> {
        val items = loadDataHelper.getDataFlow(
            getItems = {
                totalPagingSource(GalleryRepositoryImpl.IMAGES_COUNT_ON_PAGE) { pageNumber, pageSize ->
                    galleryRepository.getGalleryImages(pageNumber, pageSize).onSuccess { images ->
                        return@totalPagingSource ListWithTotal(images, pageSize)
                    }
                    return@totalPagingSource ListWithTotal(emptyList(), pageSize)
                }
            },
            mapDataItem = {
                it.toViewItem(params.selectedImages.toListUri())
            },
            getItemId = { it.cursorPosition.toLong() },
        ).cachedIn(viewModelScope)
        return items
    }

    fun onAttachFiles(attachedModel: FileSelectorViewItem) {
        if (attachedModel.isChecked)
            attachedImages.add(attachedModel)
        else
            attachedImages.removeIf { it.content.uri == attachedModel.content.uri }

        sendImagesCount(attachedImages.isNotEmpty())
    }

    private fun sendImagesCount(haveChanges: Boolean = false) {
        bottomViewAction.sendActionToBottomView(
            FileSelectorButtonEvent.AttachCountEvent(
                attachedImages.size, haveChanges
            )
        )
    }

    fun onImageClicked(fileSelectorViewItem: FileSelectorViewItem) {
        val params = ImageViewerContract.ImageViewerParams.ImageParams(
            imageModel = ImageViewerModel(
                null, null, imageGallery = fileSelectorViewItem.toGalleryImage()
            )
        )
        imageViewerLauncher.launch(params)
    }

    override fun createInitialState(): FileSelectorState = FileSelectorState()

    fun fromGalleryClicked() {
        exitWithResultFromFileSelector(
            FileSelectorNavigationContract.createResult(
                FileSelectorNavigationContract.Result.OpenGallery
            )
        )
    }

    fun fromCameraClicked() {
        exitWithResultFromFileSelector(
            FileSelectorNavigationContract.createResult(
                FileSelectorNavigationContract.Result.OpenCamera
            )
        )
    }

    fun exitNoResult() = exitWithoutResult()
    fun attachFileClicked() {
        exitWithResultFromFileSelector(
            FileSelectorNavigationContract.createResult(
                FileSelectorNavigationContract.Result.OpenFiles
            )
        )
    }

    private fun exitWithResultFromFileSelector(createResultInterface: CreateResultInterface) {
        bottomViewAction.sendActionToBottomView(FileSelectorButtonEvent.IgnoreRouterExit)
        router.exitWithResult(requestParams.resultKey, createResultInterface)
    }

}