package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.mapper.ProfileGuestMapper
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem

class ProfileGuestImagesViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params : ProfileGuestImagesNavigationContract.Params
) : BaseViewModel<ProfileGuestImagesState, ProfileGuestImagesEvent>(router, requestParams) {

    override fun createInitialState() = ProfileGuestImagesState(
        images = ProfileGuestMapper.mapFilesToRecentImages(params.images)
    )

    fun navigateBack() {
        exitWithoutResult()
    }
}