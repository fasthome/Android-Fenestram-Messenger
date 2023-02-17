package io.fasthome.component.bottom_navigation

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class BottomNavViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
) : BaseViewModel<BottomNavState, BottomNavEvent>(router, requestParams) {

    override fun createInitialState(): BottomNavState {
        return BottomNavState()
    }

}