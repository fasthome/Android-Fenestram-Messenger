package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.FilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.model.AllFilesViewItem

class ProfileGuestFilesViewModel (
    router: ContractRouter,
    requestParams: RequestParams,
) : BaseViewModel<ProfileGuestFilesState, ProfileGuestFilesEvent>(router, requestParams) {

    override fun createInitialState() = ProfileGuestFilesState(listOf())

    fun fetchFiles() {
        val files = listOf(
            AllFilesViewItem("Kek", 5, "01.05.2022"),
            AllFilesViewItem("Doc",1, "29.06.2022"),
            AllFilesViewItem("aBOBA",100, "13.07.2022")
        )
        updateState { ProfileGuestFilesState(files) }
    }

    fun navigateBack() {
        exitWithoutResult()
    }
}