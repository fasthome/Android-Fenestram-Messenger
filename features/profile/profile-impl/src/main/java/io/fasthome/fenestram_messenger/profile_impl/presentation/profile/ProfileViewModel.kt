/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.settings_api.SettingsFeature
import io.fasthome.component.permission.PermissionInterface
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
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.onSuccess
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
) : BaseViewModel<ProfileState, ProfileEvent>(router, requestParams) {

    private val settingsLauncher = registerScreen(settingsFeature.settingsNavigationContract)
    private var avatarUrl: String? = null

    init {
        viewModelScope.launch {
            profileInteractor.getPersonalData().onSuccess { personalData ->
                updateState { state ->
                    avatarUrl = if (!personalData.avatar.isNullOrEmpty() )
                        environment.endpoints.apiBaseUrl.dropLast(1) + personalData.avatar
                    else
                        null

                    state.copy(
                        fieldsData = listOf(
                            ProfileState.Field(
                                key = ProfileState.EditTextKey.BirthdateKey,
                                text = PrintableText.Raw(personalData.birth ?: ""),
                                visibility = !personalData.birth.isNullOrEmpty()
                            ),
                            ProfileState.Field(
                                key = ProfileState.EditTextKey.NicknameKey,
                                text = PrintableText.Raw(personalData.nickname ?: ""),
                                visibility = !personalData.nickname.isNullOrEmpty()
                            ),
                            ProfileState.Field(
                                key = ProfileState.EditTextKey.UsernameKey,
                                text = PrintableText.Raw(personalData.username ?: ""),
                                visibility = !personalData.username.isNullOrEmpty()
                            ),
                            ProfileState.Field(
                                key = ProfileState.EditTextKey.MailKey,
                                text = PrintableText.Raw(personalData.email ?: ""),
                                visibility = !personalData.email.isNullOrEmpty()
                            )
                        ),
                        avatarUrl = avatarUrl,
                        avatarBitmap = null
                    )
                }
            }
        }
        pickFileInterface.resultEvents()
            .onEach {
                when (it) {
                    PickFileInterface.ResultEvent.PickCancelled -> Unit
                    is PickFileInterface.ResultEvent.Picked -> {
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
                            showMessage(Message.PopUp(PrintableText.StringResource(R.string.profile_error_photo)))
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
    }

    fun checkPersonalData(name: String, nickname: String, birthday: String, mail: String) {
        updateState { state ->
            state.copy(isLoad = true)
        }
        viewModelScope.launch {
            val avatarFile = currentViewState.originalProfileImageFile
            val avatar = when {
                avatarFile != null -> {
                    profileInteractor.uploadProfileImage(avatarFile.readBytes())
                        .getOrNull()?.profileImagePath.let {
                            avatarUrl = environment.endpoints.apiBaseUrl.dropLast(1) + it
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
                username = name,
                nickname = nickname,
                birth = birthday,
                email = mail,
                avatar = avatar,
                playerId = ""
            )

            profileInteractor.sendPersonalData(
                personalData.copy(avatar = avatar)
            ).withErrorHandled(
                showErrorType = ShowErrorType.Dialog
            ) {
                showMessage(Message.PopUp(PrintableText.StringResource(R.string.profile_successs_changed)))
                updateState { state ->
                    state.copy(
                        fieldsData = listOf(
                            ProfileState.Field(
                                key = ProfileState.EditTextKey.BirthdateKey,
                                text = PrintableText.Raw(birthday),
                                visibility = !personalData.birth.isNullOrEmpty()
                            ),
                            ProfileState.Field(
                                key = ProfileState.EditTextKey.NicknameKey,
                                text = PrintableText.Raw(nickname),
                                visibility = !personalData.nickname.isNullOrEmpty()
                            ),
                            ProfileState.Field(
                                key = ProfileState.EditTextKey.UsernameKey,
                                text = PrintableText.Raw(name),
                                visibility = !personalData.username.isNullOrEmpty()
                            ),
                            ProfileState.Field(
                                key = ProfileState.EditTextKey.MailKey,
                                text = PrintableText.Raw(mail),
                                visibility = !personalData.email.isNullOrEmpty()
                            )
                        ),
                        isEdit = false,
                        isLoad = false
                    )
                }
            }
        }
    }

    override fun createInitialState(): ProfileState {
        return ProfileState(fieldsData = listOf(), null, null, null, isEdit = false, isLoad = false)
    }

    fun onAvatarClicked() {
        pickFileInterface.pickFile()
    }

    fun editClicked() {
        updateState { state ->
            state.copy(isEdit = true)
        }
    }

    fun cancelClicked() {
        updateState { state ->
            state.copy(isEdit = false)
        }
    }

    fun startSettings() {
        settingsLauncher.launch(NoParams)
    }
}