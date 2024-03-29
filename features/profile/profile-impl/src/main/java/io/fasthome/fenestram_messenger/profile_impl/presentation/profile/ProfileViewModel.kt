/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.view.View
import androidx.lifecycle.viewModelScope
import com.dolatkia.animatedThemeManager.ThemeManager
import io.fasthome.component.image_viewer.ImageViewerContract
import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.component.theme.ThemeStorage
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_impl.R
import io.fasthome.fenestram_messenger.profile_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.profile_impl.presentation.profile.model.StatusViewItem
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getPrintableRawText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class ProfileViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val profileInteractor: ProfileInteractor,
    private val profileImageUrlConverter: StorageUrlConverter,
    private val themeStorage: ThemeStorage,
    authFeature: AuthFeature,
) : BaseViewModel<ProfileState, ProfileEvent>(router, requestParams) {

    private val imageViewerLauncher = registerScreen(ImageViewerContract) {}

    private val personalDataLauncher = registerScreen(authFeature.personalDataNavigationContract) { result ->
        when (result) {
            AuthFeature.AuthResult.Canceled -> Unit
            AuthFeature.AuthResult.Success -> fetchProfile()
        }
    }

    private val statusList : List<StatusViewItem> = createStatusList()

    init {
        fetchProfile()

        onStatusSelected(statusList.first())
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
        sendEvent(ProfileEvent.AvatarLoading(true))
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

    fun onStatusSelected(statusViewItem: StatusViewItem) {
        statusList.forEach {
            it.isChecked = false
            if(statusViewItem == it){
                it.isChecked = true
            }
        }
        sendEvent(ProfileEvent.UpdateStatus(statusViewItem))
    }

    fun onStatusClicked() {
        sendEvent(ProfileEvent.ShowStatusSelectionDialog(
            items = statusList
        ))
    }

    fun createStatusList() = listOf(
        StatusViewItem(
            name = PrintableText.StringResource(R.string.status_in_office),
            isChecked = false,
            dotColor = R.color.status_green
        ),
        StatusViewItem(
            name = PrintableText.StringResource(R.string.status_in_home),
            isChecked = false,
            dotColor = R.color.status_purple
        ),
        StatusViewItem(
            name = PrintableText.StringResource(R.string.status_in_business_trip),
            isChecked = false,
            dotColor = R.color.status_blue
        ),
        StatusViewItem(
            name = PrintableText.StringResource(R.string.status_on_holiday),
            isChecked = false,
            dotColor = R.color.status_light_green
        ),
        StatusViewItem(
            name = PrintableText.StringResource(R.string.status_on_seak_leave),
            isChecked = false,
            dotColor = R.color.status_yellow
        ),
        StatusViewItem(
            name = PrintableText.StringResource(R.string.status_on_meating),
            isChecked = false,
            dotColor = R.color.status_orange
        ),
        StatusViewItem(
            name = PrintableText.StringResource(R.string.status_no_available),
            isChecked = false,
            dotColor = R.color.status_pink
        ),
    )

    fun onThemeChanged(theme: Theme, view: View) {
        ThemeManager.instance.changeTheme(theme, view)
        viewModelScope.launch {
            themeStorage.setTheme(theme)
        }
    }
}