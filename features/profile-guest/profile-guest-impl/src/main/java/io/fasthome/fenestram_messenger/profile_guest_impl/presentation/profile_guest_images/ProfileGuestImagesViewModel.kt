package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.PhotosViewItem

class ProfileGuestImagesViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
) : BaseViewModel<ProfileGuestImagesState, ProfileGuestImagesEvent>(router, requestParams) {

    override fun createInitialState() = ProfileGuestImagesState(listOf())

    fun fetchImages() {
        val images = listOf(
            PhotosViewItem(R.drawable.bg_call),
            PhotosViewItem(R.drawable.bg_account_circle),
            PhotosViewItem(R.drawable.shape_button_standart),
            PhotosViewItem(R.drawable.shape_button_standart)
        )
        updateState { ProfileGuestImagesState(images) }
    }

    fun navigateBack() {
        exitWithoutResult()
    }
}