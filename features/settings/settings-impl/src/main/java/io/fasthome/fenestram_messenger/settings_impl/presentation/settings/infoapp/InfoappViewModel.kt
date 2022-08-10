package io.fasthome.fenestram_messenger.settings_impl.presentation.settings.infoapp

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams


class InfoappViewModel(
    router: ContractRouter,
    requestParams : RequestParams,

) : BaseViewModel<InfoappState, InfoappEvent>(router,requestParams){

    override fun createInitialState(): InfoappState {
        return InfoappState()
    }

    fun backSettings() {
        exitWithoutResult()
    }

}