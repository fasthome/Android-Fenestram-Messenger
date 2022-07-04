package io.fasthome.fenestram_messenger

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.main_api.MainFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class MainActivityViewModel(
    router: ContractRouter,
    private val features: Features,
) : BaseViewModel<MainActivityState, Nothing>(requestParams = REQUEST_PARAMS, router = router) {

    class Features(
        val authFeature: AuthFeature,
        val mainFeature: MainFeature
    )

    private val authLauncher = registerScreen(features.authFeature.authNavigationContract)

    override fun createInitialState(): MainActivityState {
        return MainActivityState()
    }

    fun onAppStarted() {
        authLauncher.launch(NoParams)
    }

    companion object {
        val REQUEST_PARAMS = RequestParams(
            requestKey = "MainActivityViewModel_requestKey",
            resultKey = ContractRouter.IGNORE_RESULT,
        )
    }
}