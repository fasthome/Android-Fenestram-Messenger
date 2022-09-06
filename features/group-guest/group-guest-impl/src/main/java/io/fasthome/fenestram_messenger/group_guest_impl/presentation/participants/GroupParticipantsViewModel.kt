package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.group_guest_api.ParticipantsParams
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.mapper.userToParticipantsItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class GroupParticipantsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val params : ParticipantsParams
) : BaseViewModel<GroupParticipantsState, GroupParticipantsEvent>(router, requestParams),
    GroupParticipantsInterface {

    override fun createInitialState(): GroupParticipantsState {
        return GroupParticipantsState(
            participants = params.participants.map(::userToParticipantsItem)
        )
    }

}