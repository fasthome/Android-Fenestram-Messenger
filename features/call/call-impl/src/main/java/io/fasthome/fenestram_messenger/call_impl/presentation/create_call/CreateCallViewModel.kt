/**
 * Created by Dmitry Popov on 10.01.2023.
 */
package io.fasthome.fenestram_messenger.call_impl.presentation.create_call

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class CreateCallViewModel(
    contractRouter: ContractRouter,
    requestParams: RequestParams
) : BaseViewModel<CreateCallState, CreateCallEvent>(
    contractRouter,
    requestParams
) {

    override fun createInitialState(): CreateCallState {
        return CreateCallState()
    }

}