/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import android.content.Context
import android.content.Intent
import androidx.lifecycle.viewModelScope
import io.fasthome.component.person_detail.PersonDetail
import io.fasthome.fenestram_messenger.auth_ad_api.AuthAdFeature
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.call_api.CallFeature
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.core.debug.DebugRepo
import io.fasthome.fenestram_messenger.core.debug.EndpointsConfig
import io.fasthome.fenestram_messenger.group_guest_api.GroupGuestFeature
import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.navigation.model.createParams
import io.fasthome.fenestram_messenger.onboarding_api.OnboardingFeature
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.profile_api.entity.PersonalData
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.push_api.PushFeature
import io.fasthome.fenestram_messenger.util.getOrNull
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class DebugViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val features: Features,
    private val debugRepo: DebugRepo
) : BaseViewModel<DebugState, DebugEvent>(router, requestParams) {

    class Features(
        val authFeature: AuthFeature,
        val profileGuestFeature: ProfileGuestFeature,
        val profileFeature: ProfileFeature,
        val onboardingFeature: OnboardingFeature,
        val contactsFeature: ContactsFeature,
        val groupGuestFeature: GroupGuestFeature,
        val pushFeature: PushFeature,
        val mainFeature: MainFeature,
        val callFeature: CallFeature,
        val authAdFeature: AuthAdFeature
    )

    private var personalData: PersonalData? = null

    init {
        viewModelScope.launch {
            features.profileFeature.getPersonalData().onSuccess {
                personalData = it
            }
            val token = features.pushFeature.getPushToken()
            val userId = features.authFeature.getUserId(needLogout = false).getOrNull() ?: 0L
            val userPhone = features.authFeature.getUserPhone().getOrNull() ?: "+7"
            val userCode = features.authFeature.getUserCode().getOrNull() ?: ""
            updateState { state ->
                state.copy(
                    token = token,
                    userId = userId.toString(),
                    userCode = userCode,
                    userPhone = userPhone
                )
            }
        }
    }

    private val authLauncher = registerScreen(features.authFeature.authNavigationContract) {}

    private val authAdLauncher = registerScreen(features.authAdFeature.authAdNavigationContract) {}
    private val personalDataLauncher =
        registerScreen(features.authFeature.personalDataNavigationContract) {}
    private val profileGuestLauncher =
        registerScreen(features.profileGuestFeature.profileGuestNavigationContract) {}
    private val onboardingLauncher =
        registerScreen(features.onboardingFeature.onboardingNavigationContract) {}

    override fun createInitialState() = DebugState(
        userId = "",
        token = "",
        featuresVisible = false,
        componentsVisible = false,
        requestsVisible = false,
        loginVisible = false,
        userCode = "",
        userPhone = "",
        selectedEnv = debugRepo.endpointsConfig
    )

    fun onAuthClicked() {
        authLauncher.launch(NoParams)
    }

    fun onAuthAdClicked() {
        authAdLauncher.launch(NoParams)
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            features.authFeature.logout().withErrorHandled {}
        }
    }

    fun onProfileGuestClicked() {
        profileGuestLauncher.launch(
            ProfileGuestFeature.ProfileGuestParams(
                null,
                userName = "Example username",
                userNickname = "examplenickname",
                userAvatar = "",
                userPhone = "1234567",
                chatParticipants = listOf(),
                isGroup = false,
                editMode = false
            )
        )
    }

    fun onPersonalDataClicked() {
        personalDataLauncher.launch(
            AuthFeature.PersonalDataParams(
                username = personalData?.username,
                nickname = personalData?.nickname,
                birth = personalData?.birth,
                email = personalData?.email,
                avatar = personalData?.avatar,
                isEdit = false
            )
        )
    }

    fun onErrorDialogClicked() {
        onError(ShowErrorType.Dialog, Throwable())
    }

    fun onDeleteContactsClicked() {
        viewModelScope.launch {
            features.contactsFeature.deleteAllContacts()
                .withErrorHandled(showErrorType = ShowErrorType.Dialog, onSuccess = {
                    sendEvent(DebugEvent.ContactsDeleted)
                })
        }
    }

    fun onGroupGuestClicked() {
//        groupGuestLauncher.launch(NoParams)
    }

    fun sendTestPushNotification() {
        viewModelScope.launch {
            features.pushFeature.sendTestPush()
        }
    }

    fun updatePushToken() {
        viewModelScope.launch {
            val resutl = features.pushFeature.updateToken().successOrSendError()
            if (resutl != null) {
                val token = features.pushFeature.getPushToken()
                updateState { state ->
                    state.copy(token = token)
                }
            }
        }
    }

    fun onOnboardingClicked() {
        onboardingLauncher.launch(NoParams)
    }

    fun onLinkFieldClicked(token: String) {
        sendEvent(DebugEvent.CopyTokenEvent(token))
    }

    fun onFeaturesClicked() {
        updateState { state ->
            state.copy(featuresVisible = !state.featuresVisible)
        }
    }

    fun onComponentsClicked() {
        updateState { state ->
            state.copy(componentsVisible = !state.componentsVisible)
        }
    }

    fun onRequestsClicked() {
        updateState { state ->
            state.copy(requestsVisible = !state.requestsVisible)
        }
    }

    fun onLoginAcceptClicked(phone: String, code: String) {
        viewModelScope.launch {
            features.authFeature.login(phone, code).withErrorHandled {
                router.newRootScreen(features.mainFeature.mainNavigationContract.createParams())
            }
        }
    }

    fun onLoginClicked() {
        updateState { state ->
            state.copy(loginVisible = !state.loginVisible)
        }
    }

    fun onEnvironmentChangedClicked(config: EndpointsConfig) {
        sendEvent(DebugEvent.AcceptEnvChangeDialog(config))
    }

    fun onEnvironmentChanged(config: EndpointsConfig) {
        debugRepo.endpointsConfig = config
        sendEvent(DebugEvent.RebirthApplication)
    }

    fun onPersonDetailClicked() {
        sendEvent(
            DebugEvent.ShowPersonDetailDialog(
                PersonDetail(
                    0,
                    "",
                    "ExampleUserName",
                    "ExampleNickName",
                    "+74357274377"
                )
            )
        )
    }

    fun onSipClicked(requireContext: Context) {
        requireContext.startActivity(Intent(requireContext, features.callFeature.demoIntentActivityClass))
    }

}