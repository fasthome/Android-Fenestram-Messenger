/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.debug_impl.presentation.socket.SocketNavigationContract
import io.fasthome.fenestram_messenger.group_guest_api.GroupGuestFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature
import io.fasthome.fenestram_messenger.profile_api.entity.PersonalData
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import io.fasthome.fenestram_messenger.push_api.PushFeature
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class DebugViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val features: Features
) : BaseViewModel<DebugState, DebugEvent>(router, requestParams) {

    class Features(
        val authFeature: AuthFeature,
        val profileGuestFeature: ProfileGuestFeature,
        val profileFeature: ProfileFeature,
        val contactsFeature: ContactsFeature,
        val groupGuestFeature: GroupGuestFeature,
        val pushFeature: PushFeature
    )

    private var personalData: PersonalData? = null

    init {
        viewModelScope.launch {
            features.profileFeature.getPersonalData().onSuccess {
                personalData = it
            }
        }
    }

    private val authLauncher = registerScreen(features.authFeature.authNavigationContract) {}
    private val personalDataLauncher = registerScreen(features.authFeature.personalDataNavigationContract) {}
    private val profileGuestLauncher = registerScreen(features.profileGuestFeature.profileGuestNavigationContract) {}
    private val socketLauncher = registerScreen(SocketNavigationContract)
//    private val groupGuestLauncher = registerScreen(features.groupGuestFeature.groupGuestComponentContract)

    override fun createInitialState() = DebugState()

    fun onAuthClicked() {
        authLauncher.launch(NoParams)
    }

    fun onSocketClicked() {
        socketLauncher.launch(NoParams)
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            features.authFeature.logout().withErrorHandled {}
        }
    }

    fun onProfileGuestClicked() {
        profileGuestLauncher.launch(
            ProfileGuestFeature.ProfileGuestParams(
                userName = "Example username",
                userNickname = "examplenickname",
                userAvatar = "",
                listOf(),
                false
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
                avatar = personalData?.avatar
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

    fun updatePushToken(){
        viewModelScope.launch {
            features.pushFeature.updateToken()
        }
    }

}