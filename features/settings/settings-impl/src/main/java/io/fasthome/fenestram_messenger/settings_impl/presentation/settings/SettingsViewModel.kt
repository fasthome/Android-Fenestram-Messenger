package io.fasthome.fenestram_messenger.settings_impl.presentation.settings

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class SettingsViewModel(
    router: ContractRouter,
    requestParams : RequestParams
) : BaseViewModel<SettingsState,SettingsEvent>(router,requestParams){
    override fun createInitialState(): SettingsState {
        return SettingsState()
    }
}