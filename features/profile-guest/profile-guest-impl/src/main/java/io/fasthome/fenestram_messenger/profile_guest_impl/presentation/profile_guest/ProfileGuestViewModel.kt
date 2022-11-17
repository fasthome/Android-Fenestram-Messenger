package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import androidx.lifecycle.viewModelScope
import io.fasthome.component.imageViewer.ImageViewerContract
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.component.pick_file.ProfileImageUtil
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.logic.ProfileGuestInteractor
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.EditTextStatus
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.ProfileGuestFilesNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.ProfileGuestImagesNavigationContract
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileGuestViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ProfileGuestNavigationContract.Params,
    private val groupParticipantsInterface: GroupParticipantsInterface,
    private val messengerFeature: MessengerFeature,
    private val profileGuestInteractor: ProfileGuestInteractor,
    private val profileImageUrlConverter: StorageUrlConverter,
    private val pickFileInterface: PickFileInterface,
    private val profileImageUtil: ProfileImageUtil,
) : BaseViewModel<ProfileGuestState, ProfileGuestEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

    private val filesProfileGuestLauncher =
        registerScreen(ProfileGuestFilesNavigationContract) { result ->
            exitWithResult(
                ProfileGuestNavigationContract.createResult(
                    ProfileGuestNavigationContract.Result.Canceled
                )
            )
        }

    private val imagesProfileGuestLauncher =
        registerScreen(ProfileGuestImagesNavigationContract) { result ->
            exitWithResult(
                ProfileGuestNavigationContract.createResult(
                    ProfileGuestNavigationContract.Result.Canceled
                )
            )
        }

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.PickedImage -> {
                        val bitmap = profileImageUtil.getPhoto(it.tempFile)
                        if (bitmap != null) {
                            updateState { state ->
                                state.copy(
                                    avatarBitmap = bitmap,
                                    chatImageFile = it.tempFile
                                )
                            }
                        } else {
                            showMessage(Message.PopUp(PrintableText.StringResource(R.string.common_unable_to_download)))
                            updateState { state ->
                                state.copy(
                                    avatarBitmap = null,
                                    chatImageFile = null
                                )
                            }
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }


    override fun createInitialState() = ProfileGuestState(
        userName = PrintableText.Raw(params.userName),
        userNickname = PrintableText.Raw(params.userNickname),
        userAvatar = params.userAvatar,
        recentFiles = listOf(),
        recentImages = listOf(),
        isGroup = params.isGroup,
        userPhone = PrintableText.Raw(params.userPhone),
        editMode = params.editMode,
        avatarBitmap = null,
        chatImageFile = null,
        participantsQuantity = params.groupParticipantsParams.participants.size,
        profileGuestStatus = EditTextStatus.Idle
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

    fun onDeleteChatClicked() {
        if (params.id != null)
            sendEvent(ProfileGuestEvent.DeleteChatEvent(params.id))
    }

    fun onProfileNameChanged(newName: String) {
        if (!currentViewState.editMode) {
            return
        }
        if (newName.isEmpty()) {
            updateState { state ->
                state.copy(
                    profileGuestStatus = EditTextStatus.Error
                )
            }
        } else {
            updateState { state ->
                state.copy(
                    profileGuestStatus = EditTextStatus.Editable
                )
            }
        }
    }

    fun onEditGroupClicked(newName: String) {

        if (currentViewState.editMode) {
            viewModelScope.launch {
                if (newName.isEmpty()) {
                    return@launch
                }

                if (newName != getPrintableRawText(currentViewState.userName) &&
                    profileGuestInteractor.patchChatName(params.id!!, newName)
                        .successOrSendError() != null
                ) {
                    updateState { state -> state.copy(userName = PrintableText.Raw(newName)) }
                }

                val imageUrl: String? = currentViewState.chatImageFile?.let { file ->
                    profileGuestInteractor.uploadChatAvatar(
                        file.readBytes()
                    ).getOrNull()?.imagePath
                }?.apply { substring(20, length) }

                if (imageUrl != null && profileGuestInteractor.patchChatAvatar(
                        params.id!!,
                        imageUrl
                    )
                        .successOrSendError() != null
                ) {
                    updateState { state ->
                        state.copy(userAvatar = profileImageUrlConverter.convert(imageUrl))
                    }
                }
                updateState { state ->
                    state.copy(
                        editMode = false,
                        profileGuestStatus = EditTextStatus.Idle
                    )
                }
            }
        } else
            updateState { state ->
                state.copy(
                    editMode = true,
                    profileGuestStatus = EditTextStatus.Editable
                )
            }
    }

    fun deleteChat(id: Long) {
        viewModelScope.launch {
            if (messengerFeature.deleteChat(id).successOrSendError() != null)
                router.backTo(null)
        }
    }

    fun onAvatarClicked() {
        if (currentViewState.editMode) {
            pickFileInterface.pickFile()
        }
        else {
            if (currentViewState.userAvatar.isNotEmpty() || currentViewState.avatarBitmap != null) {
                imageViewerLauncher.launch(ImageViewerContract.ImageViewerParams.ImageParams(currentViewState.userAvatar, currentViewState.avatarBitmap))
            }
        }
    }
}
