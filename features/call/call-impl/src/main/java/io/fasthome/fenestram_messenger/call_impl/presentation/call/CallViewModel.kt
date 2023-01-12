/**
 * Created by Dmitry Popov on 10.01.2023.
 */
package io.fasthome.fenestram_messenger.call_impl.presentation.call

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class CallViewModel(
    contractRouter: ContractRouter,
    requestParams: RequestParams
) : BaseViewModel<CallState, CallEvent>(
    contractRouter,
    requestParams
) {

    override fun createInitialState(): CallState {
        return CallState()
    }

}