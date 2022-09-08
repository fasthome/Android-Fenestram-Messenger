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
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProfileViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    settingsFeature: SettingsFeature,
    private val pickFileInterface: PickFileInterface,
    private val profileInteractor: ProfileInteractor,
    private val profileImageUtil: ProfileImageUtil,
) : BaseViewModel<ProfileState, ProfileEvent>(router, requestParams) {

    private val settingsLauncher = registerScreen(settingsFeature.settingsNavigationContract)

    init {
        viewModelScope.launch {
            profileInteractor.getPersonalData().onSuccess { personalData ->
                updateState { state ->
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
                        avatarUrl = personalData.avatar
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
                        updateState { state ->
                            state.copy(
                                avatarBitmap = bitmap,
                                originalProfileImageFile = it.tempFile
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState(): ProfileState {
        return ProfileState(fieldsData = listOf(), null, null, null, false)
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