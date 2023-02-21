package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files

import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.presentation.base.navigation.OpenFileNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.mapper.ProfileGuestMapper
import io.fasthome.fenestram_messenger.util.ProgressListener
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import io.fasthome.fenestram_messenger.util.model.MetaInfo
import kotlinx.coroutines.launch

class ProfileGuestFilesViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ProfileGuestFilesNavigationContract.Params,
    private val messengerFeature: MessengerFeature,
    private val permissionInterface: PermissionInterface
) : BaseViewModel<ProfileGuestFilesState, ProfileGuestFilesEvent>(router, requestParams) {

    private val openFileLauncher = registerScreen(OpenFileNavigationContract)

    private var downloadFileJob by switchJob()

    override fun createInitialState() = ProfileGuestFilesState(
        files = ProfileGuestMapper.mapFilesToRecentDocs(params.docs)
    )

    fun filterFiles(text: String) {
        val filteredFiles = params.docs.filterIsInstance<FileItem.Document>().filter {
            it.metaInfo.name.startsWith(text.trim(), true)
        }
        updateState { state->
            state.copy(
                files = ProfileGuestMapper.mapFilesToRecentDocs(filteredFiles)
            )
        }
    }

    fun onDownloadDocument(
        meta: MetaInfo,
        progressListener: ProgressListener,
    ) {
        val documentLink = meta.url.toString()
        downloadFileJob = viewModelScope.launch {
            messengerFeature.downloadDocumentUseCase(
                documentLink = documentLink,
                progressListener = progressListener,
                metaInfo = meta,
                permissionInterface = permissionInterface,
                openFileLauncher = openFileLauncher
            )
        }
    }

    fun navigateBack() {
        exitWithoutResult()
    }
}