/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.profile_api.ProfileFeature

class ProfileViewModel(
    router : ContractRouter,
    requestParams : RequestParams
) : BaseViewModel<ProfileState, ProfileEvent>(router, requestParams) {
    override fun createInitialState(): ProfileState {
        return ProfileState()
    }

    fun checkPersonalData() {
        //TODO Выход с заполненными данными
        //exitWithResult()
    }

    fun skipPersonalData() {
        exitWithResult(ProfileNavigationContract.createResult(ProfileFeature.ProfileResult.Success))
    }

    fun fillingPersonalData(data: String, hasFocus: Boolean, key: ProfileFragment.EditTextKey) {
        if (!hasFocus && data.isNotEmpty())
            updateState { ProfileState(key, true) }
        else
            updateState { ProfileState(key, false) }
    }

    fun fillingBirthdate(data: String, hasFocus: Boolean, key: ProfileFragment.EditTextKey) {
        if (!hasFocus && data.length == 10)
            updateState { ProfileState(key, true) }
        else
            updateState { ProfileState(key, false) }

    }
}

private fun String.isNotEmpty(): Boolean {

}
