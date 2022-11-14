/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import androidx.lifecycle.viewModelScope
import io.fasthome.component.imageViewer.ImageViewerContract
import io.fasthome.component.personality_data.FillState
import io.fasthome.component.personality_data.PersonalityInterface
import io.fasthome.component.personality_data.UserDetail
import io.fasthome.component.pick_file.PickFileInterface
import io.fasthome.component.pick_file.ProfileImageUtil
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_api.entity.PersonalData
import io.fasthome.fenestram_messenger.profile_impl.R
import io.fasthome.fenestram_messenger.profile_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.settings_api.SettingsFeature
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    settingsFeature: SettingsFeature,
    private val environment: Environment,
    private val pickFileInterface: PickFileInterface,
    private val profileInteractor: ProfileInteractor,
    private val profileImageUtil: ProfileImageUtil,
    private val personalityInterface: PersonalityInterface,
) : BaseViewModel<ProfileState, ProfileEvent>(router, requestParams) {

    private val settingsLauncher = registerScreen(settingsFeature.settingsNavigationContract)
    private var updateContinueButtonJob by switchJob()
    private var avatarUrl: String? = null
    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

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
                                    avatarUrl = null,
                                    avatarBitmap = bitmap,
                                    originalProfileImageFile = it.tempFile
                                )
                            }
                        } else {
                            showMessage(Message.PopUp(PrintableText.StringResource(R.string.common_unable_to_download)))
                            updateState { state ->
                                state.copy(
                                    avatarUrl = avatarUrl,
                                    avatarBitmap = null,
                                    originalProfileImageFile = null
                                )
                            }
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

    fun fetchProfile() {
        viewModelScope.launch {
            profileInteractor.getPersonalData().onSuccess { personalData ->
                updateState { state ->
                    avatarUrl = if (!personalData.avatar.isNullOrEmpty())
                        environment.endpoints.baseUrl.dropLast(1) + personalData.avatar
                    else
                        null

                    personalityInterface.setFields(
                        UserDetail(
                            name = personalData.username ?: "",
                            mail = personalData.email ?: "",
                            birthday = personalData.birth ?: "",
                            nickname = personalData.nickname ?: ""
                        )
                    )
                    state.copy(
                        username = personalData.username ?: "",
                        avatarUrl = avatarUrl,
                        avatarBitmap = null
                    )
                }
            }
        }
    }

    fun checkPersonalData() {
        val fields = personalityInterface.getFields()
        updateState { state ->
            state.copy(isLoad = true)
        }
        viewModelScope.launch {
            val avatarFile = currentViewState.originalProfileImageFile
            val avatar = when {
                avatarFile != null -> {
                    profileInteractor.uploadProfileImage(avatarFile.readBytes())
                        .getOrNull()?.profileImagePath.let {
                            avatarUrl = environment.endpoints.baseUrl.dropLast(1) + it
                            it
                        }
                }
                !avatarUrl.isNullOrEmpty() -> {
                    avatarUrl!!.substring(20, avatarUrl!!.length)
                }
                else -> {
                    null
                }
            }

            val personalData = PersonalData(
                username = fields.name,
                nickname = fields.nickname,
                birth = fields.birthday,
                email = fields.mail,
                avatar = avatar,
                playerId = ""
            )

            when (val result = profileInteractor.sendPersonalData(
                personalData.copy(avatar = avatar)
            )) {
                is CallResult.Error -> {
                    onError(showErrorType = ShowErrorType.Dialog, throwable = result.error)
                    updateState { state ->
                        personalityInterface.runEdit(false)
                        state.copy(
                            isLoad = false,
                            isEdit = false
                        )
                    }
                }
                is CallResult.Success ->{
                    showMessage(Message.PopUp(PrintableText.StringResource(R.string.profile_successs_changed)))
                    personalityInterface.setFields(
                        UserDetail(
                            name = personalData.username ?: "",
                            mail = personalData.email ?: "",
                            birthday = personalData.birth ?: "",
                            nickname = personalData.nickname ?: ""
                        )
                    )
                    updateState { state ->
                        personalityInterface.runEdit(false)
                        state.copy(
                            username = personalData.username ?: state.username,
                            isEdit = false,
                            isLoad = false
                        )
                    }
                }
            }
        }
    }

    override fun createInitialState(): ProfileState {
        return ProfileState(
            "",
            null,
            null,
            null,
            isEdit = false,
            isLoad = false,
            readyEnabled = false
        ).also {
            personalityInterface.runEdit(false)
        }
    }

    fun onAvatarClicked() {
        if(currentViewState.isEdit) {
            pickFileInterface.pickFile()
        } else {
            if (currentViewState.avatarUrl != null || currentViewState.avatarBitmap != null) {
                imageViewerLauncher.launch(ImageViewerContract.ImageViewerParams.Params(currentViewState.avatarUrl, currentViewState.avatarBitmap))
            }
        }
    }

    fun editClicked() {
        personalityInterface.runEdit(true)
        updateState { state ->
            state.copy(isEdit = true)
        }
    }

    private fun updateFillState() {
        updateContinueButtonJob = viewModelScope.launch {
            val readyEnabled = personalityInterface.fieldStateChanges.first() == FillState.Filled
            updateState { state ->
                state.copy(
                    readyEnabled = readyEnabled
                )
            }
        }
    }

    fun onViewCreated() {
        personalityInterface.runEdit(false)
        updateState { state ->
            state.copy(isEdit = false)
        }
    }

    fun cancelClicked() {
        personalityInterface.runEdit(false)
        updateState { state ->
            state.copy(isEdit = false)
        }
        fetchProfile()
    }

    fun startSettings() {
        settingsLauncher.launch(NoParams)
    }
}