package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import android.Manifest
import android.os.Build
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import io.fasthome.component.file_selector.FileSelectorNavigationContract
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl
import io.fasthome.component.image_viewer.ImageViewerContract
import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.provideSavedState
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.presentation.base.navigation.OpenFileNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.FileItem
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.logic.ProfileGuestInteractor
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.mapper.ProfileGuestMapper
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.EditTextStatus
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.TextViewKey
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.ProfileGuestFilesNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.ProfileGuestImagesNavigationContract
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.mapper.ImagesMapper
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import io.fasthome.fenestram_messenger.util.model.MetaInfo
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class ProfileGuestViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: ProfileGuestNavigationContract.Params,
    private val profileGuestInteractor: ProfileGuestInteractor,
    private val profileImageUrlConverter: StorageUrlConverter,
    private val savedStateHandle: SavedStateHandle,
    private val features: Features,
    private val permissionInterface: PermissionInterface,
    private val groupParticipantsInterface: GroupParticipantsInterface,
    private val pickFileInterface: PickFileInterface,
    private val messengerFeature: MessengerFeature,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<ProfileGuestState, ProfileGuestEvent>(router, requestParams) {

    class Features(
        val messengerFeature: MessengerFeature,
        val contactsFeature: ContactsFeature,
    )

    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

    private val openFileLauncher = registerScreen(OpenFileNavigationContract)

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

    private val fileSelectorLauncher = registerScreen(FileSelectorNavigationContract) { result ->
        when (result) {
            is FileSelectorNavigationContract.Result.Attach -> {
                viewModelScope.launch {
                    val imageContent =
                        result.images.firstOrNull() as? UriLoadableContent ?: return@launch
                    galleryRepository.getFileFromGalleryUri(imageContent.uri)
                        .onSuccess { imageFile ->
                            GalleryRepositoryImpl
                            updateState { state ->
                                state.copy(
                                    avatarContent = Content.FileContent(imageFile),
                                    chatImageFile = imageFile
                                )
                            }
                        }
                }
            }
            FileSelectorNavigationContract.Result.OpenCamera -> {
                sendEvent(ProfileGuestEvent.OpenCamera)
            }
            FileSelectorNavigationContract.Result.OpenGallery -> {
                sendEvent(ProfileGuestEvent.OpenImagePicker)
            }
        }
    }

    private var imageFiles: List<FileItem> = listOf()
    private var documentFiles: List<FileItem> = listOf()
    private var downloadFileJob by switchJob()

    init {
        groupParticipantsInterface.listChanged = { size ->
            updateState {
                it.copy(participantsQuantity = size)
            }
        }

        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.PickedImage -> {
                        updateState { state ->
                            state.copy(
                                avatarContent = Content.FileContent(it.tempFile),
                                chatImageFile = it.tempFile
                            )
                        }
                    }
                    is PickFileInterface.ResultEvent.PickedFile -> {}
                }
            }
            .launchIn(viewModelScope)
    }


    override fun createInitialState(): ProfileGuestState {
        val savedState = savedStateHandle.provideSavedState {
            SavedState(
                runEdit = currentViewState.editMode,
                avatarContent = currentViewState.avatarContent
            )
        }
        return ProfileGuestState(
            userName = PrintableText.Raw(params.userName),
            userNickname = PrintableText.Raw(params.userNickname),
            userAvatar = params.userAvatar,
            recentFiles = listOf(),
            recentImages = listOf(),
            recentImagesCount = PrintableText.EMPTY,
            recentFilesCount = PrintableText.EMPTY,
            isGroup = params.isGroup,
            userPhone = PrintableText.Raw(params.userPhone),
            editMode = savedState?.runEdit ?: params.editMode,
            avatarContent = savedState?.avatarContent,
            chatImageFile = null,
            participantsQuantity = params.groupParticipantsParams.participants.size,
            profileGuestStatus = EditTextStatus.Idle
        )
    }


    fun fetchFilesAndPhotos(spanCount: Int) {
        viewModelScope.launch {
            val chatId = params.groupParticipantsParams.chatId ?: return@launch

            val images =
                profileGuestInteractor.getAttachImages(chatId = chatId).getOrDefault(listOf())

            imageFiles = images.reversed()

            val files =
                profileGuestInteractor.getAttachFiles(chatId = chatId).getOrDefault(listOf())

            documentFiles = files.reversed()

            updateState { state ->
                state.copy(
                    recentImages = ProfileGuestMapper.mapPreviewFilesToRecentImages(
                        spanCount,
                        imageFiles
                    ),
                    recentImagesCount = PrintableText.PluralResource(
                        R.plurals.image_quantity,
                        images.size,
                        images.size
                    ),
                    recentFiles = ProfileGuestMapper.mapPreviewFilesToRecentFiles(documentFiles),
                    recentFilesCount = PrintableText.PluralResource(
                        R.plurals.file_quantity,
                        files.size,
                        files.size
                    ),
                )
            }
        }
    }

    fun onShowFilesClicked() {
        filesProfileGuestLauncher.launch(
            ProfileGuestFilesNavigationContract.Params(
                docs = documentFiles
            )
        )
    }

    fun onShowPhotosClicked() {
        imagesProfileGuestLauncher.launch(
            ProfileGuestImagesNavigationContract.Params(
                images = imageFiles
            )
        )
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

    fun onEditClicked(newName: String) {
        viewModelScope.launch {
            if (newName.isEmpty()) {
                return@launch
            }
            sendEvent(ProfileGuestEvent.Loading(isLoading = true))

            if (currentViewState.editMode && params.isGroup) {
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
                }

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
            } else if (currentViewState.editMode && !params.isGroup) {
                val readPermissionGranted =
                    permissionInterface.request(Manifest.permission.READ_CONTACTS)
                val writePermissionGranted =
                    permissionInterface.request(Manifest.permission.WRITE_CONTACTS)
                val currentName = getPrintableRawText(currentViewState.userName)

                if (newName != currentName && writePermissionGranted && readPermissionGranted) {
                    features.contactsFeature.updateContactName(
                        params.userPhone,
                        currentName,
                        newName
                    )
                    updateState { state -> state.copy(userName = PrintableText.Raw(newName)) }
                }
                updateState { state ->
                    state.copy(
                        editMode = false,
                        profileGuestStatus = EditTextStatus.Idle
                    )
                }
            } else
                updateState { state ->
                    state.copy(
                        editMode = true,
                        profileGuestStatus = EditTextStatus.Editable
                    )
                }
            sendEvent(ProfileGuestEvent.Loading(isLoading = false))
        }
    }

    fun deleteChat(id: Long) {
        viewModelScope.launch {
            if (features.messengerFeature.deleteChat(id).successOrSendError() != null)
                router.backTo(null)
        }
    }

    fun onAvatarClicked() {
        if (currentViewState.editMode && params.isGroup) {
            viewModelScope.launch {
                val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionInterface.request(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    permissionInterface.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (permissionGranted) {
                    fileSelectorLauncher.launch(
                        FileSelectorNavigationContract.Params(
                            selectedImages = emptyList(),
                            maxImagesCount = FileSelectorNavigationContract.Params.IMAGES_COUNT_ONE,
                            canSelectFiles = false
                        )
                    )
                }
            }
        } else {
            if (currentViewState.userAvatar.isNotEmpty() || currentViewState.avatarContent != null) {
                imageViewerLauncher.launch(
                    ImageViewerContract.ImageViewerParams.ImageParams(
                        ImageViewerModel(
                            currentViewState.userAvatar,
                            currentViewState.avatarContent
                        )
                    )
                )
            }
        }
    }

    fun onItemClicked(position: Int) {
        imageViewerLauncher.launch(
            ImageViewerContract.ImageViewerParams.ImagesParams(
                imageViewerModel = imageFiles
                    .filterIsInstance<FileItem.Image>()
                    .map { ImagesMapper.toImageViewerItem(it) },
                currentImagePosition = position
            )
        )
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

    override fun onBackPressed(): Boolean {
        exitWithNameChanged()
        return super.onBackPressed()
    }

    fun exitWithNameChanged() {
        val currentName = getPrintableRawText(currentViewState.userName)
        if (currentName != params.userName) {
            exitWithResult(
                ProfileGuestNavigationContract.createResult(
                    ProfileGuestNavigationContract.Result.ChatNameChanged(currentViewState.userName)
                )
            )
        }
    }

    fun copyText(text: String, textViewKey: TextViewKey) {
        if (currentViewState.editMode) return

        val toastMessage = when {
            textViewKey == TextViewKey.Nickname && !currentViewState.isGroup -> PrintableText.StringResource(
                R.string.common_nickname_copied
            )
            textViewKey == TextViewKey.Phone -> PrintableText.StringResource(R.string.common_phone_copied)
            textViewKey == TextViewKey.Name && currentViewState.isGroup -> PrintableText.StringResource(
                R.string.common_chat_name_copied
            )
            textViewKey == TextViewKey.Name && !currentViewState.isGroup -> PrintableText.StringResource(
                R.string.common_name_copied
            )
            else -> return
        }

        sendEvent(ProfileGuestEvent.CopyText(text, toastMessage))
    }

    fun selectFromCamera() {
        pickFileInterface.launchCamera()
    }

    fun selectFromGallery() {
        pickFileInterface.pickFile()
    }


    @Parcelize
    class SavedState(
        val avatarContent: Content?,
        val runEdit: Boolean,
    ) : Parcelable
}
