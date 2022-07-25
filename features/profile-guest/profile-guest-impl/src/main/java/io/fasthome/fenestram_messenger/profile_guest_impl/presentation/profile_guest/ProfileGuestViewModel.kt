package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.FilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.PhotosViewItem

class ProfileGuestViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
) : BaseViewModel<ProfileGuestState, ProfileGuestEvent>(router, requestParams) {

    override fun createInitialState() = ProfileGuestState(listOf(), listOf())

    fun fetchFilesAndPhotos() {
        val files = listOf(
            FilesViewItem("Kek"), FilesViewItem("Doc"),
            FilesViewItem("aBOBA")
        )

        val photos = listOf(
            PhotosViewItem(R.drawable.bg_call),
            PhotosViewItem(R.drawable.bg_account_circle),
            PhotosViewItem(R.drawable.shape_button_standart),
            PhotosViewItem(R.drawable.shape_button_standart)
        )
        updateState { ProfileGuestState(files, photos) }
    }
}