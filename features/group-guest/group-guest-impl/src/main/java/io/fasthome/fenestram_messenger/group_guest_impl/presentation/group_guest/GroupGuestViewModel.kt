/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class GroupGuestViewModel(
    requestParams: RequestParams,
    router: ContractRouter
) : BaseViewModel<GroupGuestState, GroupGuestEvent>(router, requestParams) {

    override fun createInitialState(): GroupGuestState {
        return GroupGuestState()
    }

}