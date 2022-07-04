/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.presentation.main

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class MainViewModel(
    router: ContractRouter,
    requestParams: RequestParams) : BaseViewModel<MainState, MainEvent>(router, requestParams) {

    override fun createInitialState(): MainState {
        return MainState()
    }

}