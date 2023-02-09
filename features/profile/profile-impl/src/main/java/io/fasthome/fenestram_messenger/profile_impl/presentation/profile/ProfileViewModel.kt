/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import androidx.lifecycle.viewModelScope
import io.fasthome.component.image_viewer.ImageViewerContract
import io.fasthome.component.image_viewer.ImageViewerModel
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class ProfileViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val profileInteractor: ProfileInteractor,
    private val profileImageUrlConverter: StorageUrlConverter,
    authFeature: AuthFeature,
) : BaseViewModel<ProfileState, ProfileEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

    private val personalDataLauncher = registerScreen(authFeature.personalDataNavigationContract) { result ->
        when (result) {
            AuthFeature.AuthResult.Canceled -> Unit
            AuthFeature.AuthResult.Success -> fetchProfile()
        }
    }

    init {
        fetchProfile()
    }

    override fun createInitialState(): ProfileState {
        return ProfileState(
            username = null,
            nickname = null,
            birth = null,
            email = null,
            avatarUrl = null
        )
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            profileInteractor.getPersonalData().onSuccess { personalData ->
                updateState { state ->
                    val avatarUrl = if (!personalData.avatar.isNullOrEmpty())
                        profileImageUrlConverter.convert(personalData.avatar)
                    else
                        null

                    state.copy(
                        username = PrintableText.Raw(personalData.username ?: ""),
                        nickname = PrintableText.Raw(personalData.nickname ?: ""),
                        birth = PrintableText.Raw(personalData.birth ?: ""),
                        email = PrintableText.Raw(personalData.email ?: ""),
                        avatarUrl = avatarUrl
                    )
                }
            }
        }
    }

    fun onAvatarClicked() {
        imageViewerLauncher.launch(
            ImageViewerContract.ImageViewerParams.ImageParams(
                ImageViewerModel(
                    imageUrl = currentViewState.avatarUrl,
                    imageContent = null
                )
            )
        )
    }

    fun editClicked() {
        personalDataLauncher.launch(
            AuthFeature.PersonalDataParams(
                username = getPrintableRawText(currentViewState.username),
                nickname = getPrintableRawText(currentViewState.nickname),
                birth = getPrintableRawText(currentViewState.birth),
                email = getPrintableRawText(currentViewState.email),
                avatar = profileImageUrlConverter.extractPath(currentViewState.avatarUrl),
                isEdit = true
            )
        )
    }
}