/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class ProfileViewModel(
    router : ContractRouter,
    requestParams : RequestParams
) : BaseViewModel<ProfileState, ProfileEvent>(router, requestParams) {
    override fun createInitialState(): ProfileState {
        return ProfileState()
    }
}