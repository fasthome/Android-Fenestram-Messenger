/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.debug_api.DebugFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class DebugViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    val features : Features
) : BaseViewModel<DebugState, DebugEvent>(router, requestParams) {

    class Features(
        val authFeature : AuthFeature
    )

    private val authLauncher = registerScreen(features.authFeature.authNavigationContract) {}

    override fun createInitialState() = DebugState()

    fun onAuthClicked() {
        authLauncher.launch(NoParams)
    }

}