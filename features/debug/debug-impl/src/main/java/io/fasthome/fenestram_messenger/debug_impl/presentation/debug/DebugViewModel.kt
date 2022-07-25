/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.debug_api.DebugFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_guest_api.ProfileGuestFeature
import kotlinx.coroutines.launch

class DebugViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    val features : Features
) : BaseViewModel<DebugState, DebugEvent>(router, requestParams) {

    class Features(
        val authFeature : AuthFeature,
        val profileGuestFeature: ProfileGuestFeature
    )

    private val authLauncher = registerScreen(features.authFeature.authNavigationContract) {}
    private val profileGuestLauncher = registerScreen(features.profileGuestFeature.profileGuestNavigationContract) {}

    override fun createInitialState() = DebugState()

    fun onAuthClicked() {
        authLauncher.launch(NoParams)
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            features.authFeature.logout().withErrorHandled {}
        }
    }

    fun onProfileGuestClicked() {
        profileGuestLauncher.launch(NoParams)
    }

}