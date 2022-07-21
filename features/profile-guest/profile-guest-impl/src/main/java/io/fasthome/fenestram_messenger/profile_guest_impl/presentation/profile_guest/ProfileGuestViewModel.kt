package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ProfileGuestViewModel (
    router: ContractRouter,
    requestParams: RequestParams,
) : BaseViewModel<ProfileGuestState, ProfileGuestEvent>(router, requestParams) {

    override fun createInitialState() = ProfileGuestState()
}