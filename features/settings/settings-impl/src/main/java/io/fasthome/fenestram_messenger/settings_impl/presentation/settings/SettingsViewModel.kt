package io.fasthome.fenestram_messenger.settings_impl.presentation.settings


import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.settings_impl.presentation.settings.infoapp.InfoappNavigationContact
import kotlinx.coroutines.launch


class SettingsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val features : Features
) : BaseViewModel<SettingsState, SettingsEvent>(router, requestParams) {

    class Features(
        val authFeature: AuthFeature,
    )

    override fun createInitialState(): SettingsState {
        return SettingsState(blueSelected = false, greenSelected = true)
    }

    private val infoappLauncher = registerScreen(InfoappNavigationContact)


    fun backProfile() {
        exitWithoutResult()
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            features.authFeature.logout().withErrorHandled {}
        }
    }

    fun startInfoapp() {
        infoappLauncher.launch(NoParams)
    }

    fun onGreenClicked() {
        updateState { state ->
            state.copy(blueSelected = true, greenSelected = false)
        }
    }

    fun onBlueClicked() {
        updateState { state ->
            state.copy(greenSelected = true, blueSelected = false)
        }
    }


}