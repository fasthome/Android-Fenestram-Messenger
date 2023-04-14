/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info

import android.Manifest
import android.os.Build
import androidx.lifecycle.viewModelScope
import io.fasthome.component.gallery.GalleryRepository
import io.fasthome.component.gallery.GalleryRepositoryImpl
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.component.pick_file.ProfileImageUtil
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info.model.AvatarImage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.mapper.mapToContactViewItem
import io.fasthome.component.file_selector.FileSelectorNavigationContract
import io.fasthome.component.file_selector.FileSelectorNavigationContract.Params.Companion.IMAGES_COUNT_ONE
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UriLoadableContent
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CreateInfoViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params: CreateInfoContract.Params,
    private val pickFileInterface: PickFileInterface,
    private val profileImageUtil: ProfileImageUtil,
    private val permissionInterface: PermissionInterface,
    private val galleryRepository: GalleryRepository,
) : BaseViewModel<CreateInfoState, CreateInfoEvent>(router, requestParams) {

    private val conversationLauncher = registerScreen(ConversationNavigationContract) { }

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.PickedImage -> {
                        val bitmap = profileImageUtil.getPhoto(it.tempFile)
                        if (bitmap != null) {
                            updateState { state ->
                                state.copy(avatarImage = AvatarImage(bitmap, it.tempFile))
                            }
                        }
                    }
                    is PickFileInterface.ResultEvent.PickedFile -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    private val fileSelectorLauncher = registerScreen(FileSelectorNavigationContract) { result ->
        when (result) {
            is FileSelectorNavigationContract.Result.Attach -> {
                viewModelScope.launch {
                    val imageContent = result.images.firstOrNull() as? UriLoadableContent ?: return@launch
                        galleryRepository.getFileFromGalleryUri(imageContent.uri).onSuccess { imageFile ->
                            val bitmap = profileImageUtil.getPhoto(imageFile)
                            GalleryRepositoryImpl
                            if (bitmap != null) {
                                updateState { state ->
                                    state.copy(avatarImage = AvatarImage(bitmap, imageFile))
                                }
                            }
                        }
                }
            }
            FileSelectorNavigationContract.Result.OpenCamera -> {
                sendEvent(CreateInfoEvent.OpenCamera)
            }
            FileSelectorNavigationContract.Result.OpenGallery -> {
                sendEvent(CreateInfoEvent.OpenImagePicker)
            }
        }
    }

    override fun createInitialState(): CreateInfoState {
        return CreateInfoState(params.contacts.map(::mapToContactViewItem), null)
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

    fun onReadyClicked(chatName: String) {
        if (chatName.isEmpty()) {
            showPopup(PrintableText.StringResource(R.string.messenger_input_chat_name))
            return
        }

        viewModelScope.launch {
            var avatarBytes = byteArrayOf()
            currentViewState.avatarImage?.let { avatarImage ->
                avatarBytes = avatarImage.file.readBytes()
            }

            router.backTo(null)
            conversationLauncher.launch(
                ConversationNavigationContract.Params(
                    avatarBytes = avatarBytes,
                    chat = Chat(
                        null,
                        name = chatName,
                        users = params.contacts.map { it.userId ?: 0 },
                        messages = listOf(),
                        time = null,
                        avatar = null,
                        isGroup = true,
                        pendingMessages = 0
                    )
                )
            )
        }
    }

    fun onAvatarClicked() {
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
                        maxImagesCount = IMAGES_COUNT_ONE,
                        canSelectFiles = false
                    )
                )
            }
        }
    }

    fun selectFromCamera() {
        pickFileInterface.launchCamera()
    }

    fun selectFromGallery() {
        pickFileInterface.pickFile()
    }


}