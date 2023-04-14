package io.fasthome.fenestram_messenger.settings_impl.presentation.infoapp

import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.presentation.base.navigation.OpenUrlNavigationContract
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.util.PrintableText


class InfoappViewModel(
    router: ContractRouter,
    requestParams : RequestParams,
    private val environment: Environment
) : BaseViewModel<InfoappState, InfoappEvent>(router,requestParams){

    private val openUrlLauncher = registerScreen(OpenUrlNavigationContract)

    override fun createInitialState(): InfoappState {
        return InfoappState(
            version = PrintableText.StringResource(R.string.app_version, environment.appVersion)
        )
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

    fun rulesClicked(){
        openUrlLauncher.launch(OpenUrlNavigationContract.Params(environment.endpoints.policyUrl))
    }
}