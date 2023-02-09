package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images

import io.fasthome.component.image_viewer.ImageViewerContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.mapper.ProfileGuestMapper
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.mapper.ImagesMapper

class ProfileGuestImagesViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ProfileGuestImagesNavigationContract.Params
) : BaseViewModel<ProfileGuestImagesState, ProfileGuestImagesEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

    override fun createInitialState() = ProfileGuestImagesState(
        images = ProfileGuestMapper.mapFilesToRecentImages(params.images)
    )

    fun onItemClicked(position: Int) {
        imageViewerLauncher.launch(
            ImageViewerContract.ImageViewerParams.ImagesParams(
                imageViewerModel = params.images
                    .filterIsInstance<FileItem.Image>()
                    .map { ImagesMapper.toImageViewerItem(it) },
                currentImagePosition = position
            )
        )
    }

    fun navigateBack() {
        exitWithoutResult()
    }
}