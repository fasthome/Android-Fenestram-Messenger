package io.fasthome.component.profile_verification

import android.Manifest
import android.os.Build
import androidx.lifecycle.viewModelScope
import io.fasthome.component.file_selector.FileSelectorNavigationContract
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl
import io.fasthome.component.image_viewer.ImageViewerContract
import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class ProfileVerificationViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val permissionInterface: PermissionInterface,
    private val galleryRepository: GalleryRepository,
    private val pickFileInterface: PickFileInterface,
    private val profileImageUrlConverter: StorageUrlConverter,
) : BaseViewModel<ProfileVerificationState, ProfileVerificationEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

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
                                    imageFile = imageFile
                                )
                            }
                        }
                }
            }
            FileSelectorNavigationContract.Result.OpenCamera -> {
                sendEvent(ProfileVerificationEvent.OpenCamera)
            }
            FileSelectorNavigationContract.Result.OpenGallery -> {
                sendEvent(ProfileVerificationEvent.OpenImagePicker)
            }
        }
    }

    override fun createInitialState(): ProfileVerificationState {
        return ProfileVerificationState(
            userName = PrintableText.Raw("Мария Сергеева"),
            email = PrintableText.Raw("email@email.com"),
            speciality = PrintableText.Raw("IOS Разработчик"),
            birthday = null,
            department = PrintableText.Raw("IT Департамент")
        )
    }

    fun onEditAvatarClicked() {
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
    }

    fun onDoneClicked() {
        viewModelScope.launch {
            val imageUrl: String? = currentViewState.imageFile?.let { file ->
                /* // TODO: UploadAvatar
            .uploadChatAvatar(
                file.readBytes()
            ).getOrNull()?.imagePath
            }
            updateState { state ->
                state.copy(userAvatar = profileImageUrlConverter.convert(imageUrl))
            } */
                ""
            }
            exitWithoutResult()
        }
    }

    fun selectFromCamera() {
        pickFileInterface.launchCamera()
    }

    fun selectFromGallery() {
        pickFileInterface.pickFile()
    }


    fun onImageClicked() {
        if(currentViewState.userAvatar == null && currentViewState.avatarContent == null) return
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