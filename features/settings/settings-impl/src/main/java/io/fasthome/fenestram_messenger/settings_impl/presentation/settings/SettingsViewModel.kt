package io.fasthome.fenestram_messenger.settings_impl.presentation.settings


import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch


class SettingsViewModel(
    router: ContractRouter,
    requestParams : RequestParams,
    private val authFeature: AuthFeature
) : BaseViewModel<SettingsState,SettingsEvent>(router,requestParams){



    override fun createInitialState(): SettingsState {
        return SettingsState()
    }

    fun backProfile() {
        exitWithoutResult()
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            authFeature.logout().withErrorHandled {}
        }
    }







}