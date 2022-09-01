package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.group_guest_api.GroupGuestFeature
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.ProfileGuestFilesNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.ProfileGuestImagesNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

class ProfileGuestViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ProfileGuestNavigationContract.Params,
    private val groupParticipantsInterface : GroupParticipantsInterface
) : BaseViewModel<ProfileGuestState, ProfileGuestEvent>(router, requestParams) {

    private val filesProfileGuestLauncher = registerScreen(ProfileGuestFilesNavigationContract) { result ->
        exitWithResult(ProfileGuestNavigationContract.createResult(result))
    }

    private val imagesProfileGuestLauncher = registerScreen(ProfileGuestImagesNavigationContract) { result ->
        exitWithResult(ProfileGuestNavigationContract.createResult(result))
    }

    override fun createInitialState() =
        ProfileGuestState(
            userName = PrintableText.Raw(params.userName),
            userNickname = PrintableText.Raw(params.userNickname),
            userAvatar = params.userAvatar,
            recentFiles = listOf(),
            recentImages = listOf(),
            isGroup = params.isGroup,
        )

    fun fetchFilesAndPhotos() {
        val files = listOf(
            RecentFilesViewItem("Kek"), RecentFilesViewItem("Doc"),
            RecentFilesViewItem("aBOBA"), RecentFilesViewItem("Doc2")
        )

        val photos = listOf(
            RecentImagesViewItem(R.drawable.shape_button_standart, 0, false),
            RecentImagesViewItem(R.drawable.shape_button_standart, 0, false),
            RecentImagesViewItem(R.drawable.shape_button_standart, 0, false),
            RecentImagesViewItem(R.drawable.shape_button_standart, 0, false),
            RecentImagesViewItem(R.drawable.shape_button_standart, 0, false)
        )

        updateState { state ->
            state.copy(
                recentImages = photos,
                recentFiles = files
            )
        }
    }

    fun onShowFilesClicked() {
        filesProfileGuestLauncher.launch(NoParams)
    }

    fun onShowPhotosClicked() {
        imagesProfileGuestLauncher.launch(NoParams)
    }
}