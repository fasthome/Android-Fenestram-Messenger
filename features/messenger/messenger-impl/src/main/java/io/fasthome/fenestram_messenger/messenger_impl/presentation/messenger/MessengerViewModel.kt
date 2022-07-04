/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class MessengerViewModel(
    router : ContractRouter,
    requestParams : RequestParams
) : BaseViewModel<MessengerState, MessengerEvent>(router, requestParams) {
    override fun createInitialState(): MessengerState {
        return MessengerState()
    }
}