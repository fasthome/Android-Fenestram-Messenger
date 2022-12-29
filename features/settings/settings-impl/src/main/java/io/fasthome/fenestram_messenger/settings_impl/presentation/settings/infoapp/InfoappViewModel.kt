package io.fasthome.fenestram_messenger.settings_impl.presentation.settings.infoapp

import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.presentation.base.navigation.OpenUrlNavigationContract


class InfoappViewModel(
    router: ContractRouter,
    requestParams : RequestParams,
    private val environment: Environment
) : BaseViewModel<InfoappState, InfoappEvent>(router,requestParams){

    private val openUrlLauncher = registerScreen(OpenUrlNavigationContract)

    override fun createInitialState(): InfoappState {
        return InfoappState()
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

    fun rulesClicked(){
        openUrlLauncher.launch(OpenUrlNavigationContract.Params(environment.endpoints.policyUrl))
    }
}