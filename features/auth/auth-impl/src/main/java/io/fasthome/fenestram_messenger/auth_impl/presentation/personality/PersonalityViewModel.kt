package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import io.fasthome.component.camera.CameraComponentParams
import io.fasthome.component.gallery.GalleryOperations
import io.fasthome.component.personality_data.FillState
import io.fasthome.component.personality_data.PersonalityInterface
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_api.UserDetail
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.camera_api.*
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.mvi.provideSavedState
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.profile_api.entity.PersonalData
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import io.fasthome.fenestram_messenger.util.model.Bytes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.*
import kotlin.coroutines.suspendCoroutine


class PersonalityViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val profileFeature: ProfileFeature,
    private val pickFileInterface: PickFileInterface,
    private val personalityInterface: PersonalityInterface,
    private val params: PersonalityNavigationContract.Params,
    private val cameraFeature: CameraFeature,
    private val profileImageUrlConverter: StorageUrlConverter,
    private val savedStateHandle: SavedStateHandle,
    private val galleryOperations: GalleryOperations
) : BaseViewModel<PersonalityState, PersonalityEvent>(router, requestParams) {

    private var avatarUrl: String? = null
    private var updateContinueButtonJob by switchJob()
    private var secondTimeClicked = false

    private val cameraLauncher =
        registerScreen(cameraFeature.cameraNavigationContract) { result ->
            val tempFile = result.tempFile

            photoPreviewLauncher.launch(
                ConfirmParams(
                    content = Content.FileContent(tempFile),
                )
            )
        }

    private val photoPreviewLauncher =
        registerScreen(cameraFeature.confirmNavigationContract) { result ->
            when (val action = result.action) {
                is ConfirmResult.Action.Confirm -> {
                    saveFile(action.tempFile)
                }
                ConfirmResult.Action.Retake -> selectFromCamera()
                ConfirmResult.Action.Cancel -> Unit
            }
        }

    private val fileCapturedItem: CapturedItem
        get() = CapturedItem(UUID.randomUUID().toString())

    init {
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.PickedImage -> {
                        updateState { state ->
                            state.copy(
                                avatarContent = Content.FileContent(it.tempFile),
                                originalProfileImageFile = it.tempFile,
                                profileImageUrl = null
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)

        personalityInterface.fieldStateChanges
            .onEach {
                updateFillState()
            }
            .launchIn(viewModelScope)
    }

    private fun updateFillState() {
        updateContinueButtonJob = viewModelScope.launch {
            val readyEnabled = personalityInterface.fieldStateChanges.first() == FillState.Filled || currentViewState.avatarContent != null
            updateState { state ->
                state.copy(
                    readyEnabled = readyEnabled
                )
            }
        }
    }

    override fun createInitialState(): PersonalityState {
        val userDetail = params.userDetail

        val savedState = savedStateHandle.provideSavedState {
            SavedState(
                userDetail = userDetail
            )
        }

        val toUseDetail = savedState?.userDetail ?: userDetail

        avatarUrl = if (!userDetail.profileImageUrl.isNullOrEmpty())
            profileImageUrlConverter.convert(userDetail.profileImageUrl)
        else
            null

        return PersonalityState(
            createFieldsList(toUseDetail),
            avatarContent = null,
            originalProfileImageFile = null,
            profileImageUrl = avatarUrl,
            readyEnabled = false,
            label = if (params.isEdit) {
                PrintableText.StringResource(R.string.common_edit)
            } else {
                PrintableText.StringResource(R.string.common_welcome_message_label)
            }
        )
    }

    fun checkPersonalData() {
        viewModelScope.launch {
            sendEvent(PersonalityEvent.Loading(isLoading = true))
            val avatarContent = currentViewState.avatarContent

            val avatar = when {
                avatarContent != null -> {
                    when (avatarContent) {
                        is Content.FileContent -> {
                            profileFeature.uploadProfileImage(avatarContent.file.readBytes())
                                .getOrNull()?.profileImagePath.let {
                                    avatarUrl = profileImageUrlConverter.convert(it)
                                    it
                                }
                        }
                        is Content.LoadableContent -> {
                            profileFeature.uploadProfileImage(avatarContent.load()?.array ?: byteArrayOf())
                                .getOrNull()?.profileImagePath.let {
                                    avatarUrl = profileImageUrlConverter.convert(it)
                                    it
                                }
                        }
                    }
                }
                !avatarUrl.isNullOrEmpty() -> {
                    profileImageUrlConverter.extractPath(avatarUrl)
                }
                else -> {
                    null
                }
            }

            val playerId = getFirebaseToken()

            val userData = personalityInterface.getFields()
            val personalData = PersonalData(
                username = userData.name,
                nickname = userData.nickname,
                birth = userData.birth,
                email = userData.email,
                avatar = avatar,
                playerId = playerId
            )

            profileFeature.sendPersonalData(
                personalData.copy(avatar = avatar)
            ).withErrorHandled(
                showErrorType = ShowErrorType.Dialog
            ) {
                exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
            }
            sendEvent(PersonalityEvent.Loading(isLoading = false))
        }
    }

    fun skipPersonalData() {
        exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
    }

    fun onSelectPhotoClicked() {
        sendEvent(PersonalityEvent.ShowSelectFromDialog)
    }

    private suspend fun getFirebaseToken() = suspendCoroutine<String> { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                val token: String = task.result
                continuation.resumeWith(Result.success(token))
            }.addOnFailureListener {
                continuation.resumeWith(Result.failure(it))
            }
    }

    override fun onBackPressed(): Boolean {
        if (params.auth) {
            exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
            return params.auth
        }

        if (!secondTimeClicked) {
            secondTimeClicked = !secondTimeClicked
            return secondTimeClicked
        }

        router.finishChain()
        return secondTimeClicked
    }

    fun selectFromCamera() {
        cameraLauncher.launch(
            CameraParams(
                cameraComponentParams = CameraComponentParams(
                    maxPhotoSize = Bytes(Bytes.BYTES_PER_MB),
                    fixedOrientation = null,
                    copyToExternalDir = true
                )
            )
        )
    }

    fun selectFromGallery() {
        pickFileInterface.pickFile()
    }

    private fun saveFile(tempFile: File) {
        viewModelScope.launch {
            val content = fileCapturedItem.id.let {
                cameraFeature.saveFile(it, tempFile)
                PhotoLoadableContent(it)
            }
            updateState { state ->
                state.copy(
                    avatarContent = content
                )
            }
        }
    }

    private fun createFieldsList(userDetail : UserDetail) =
        listOf(
            PersonalityState.Field(
                key = PersonalityFragment.EditTextKey.BirthdateKey,
                text = PrintableText.Raw(userDetail.birth),
                visibility = userDetail.birth.isNotEmpty()
            ),
            PersonalityState.Field(
                key = PersonalityFragment.EditTextKey.UserNameKey,
                text = PrintableText.Raw(userDetail.nickname),
                visibility = userDetail.nickname.isNotEmpty()
            ),
            PersonalityState.Field(
                key = PersonalityFragment.EditTextKey.NameKey,
                text = PrintableText.Raw(userDetail.name),
                visibility = userDetail.name.isNotEmpty()
            ),
            PersonalityState.Field(
                key = PersonalityFragment.EditTextKey.MailKey,
                text = PrintableText.Raw(userDetail.email),
                visibility = userDetail.email.isNotEmpty()
            )
        )


    @Parcelize
    class SavedState(
        val userDetail: UserDetail
    ) : Parcelable

}