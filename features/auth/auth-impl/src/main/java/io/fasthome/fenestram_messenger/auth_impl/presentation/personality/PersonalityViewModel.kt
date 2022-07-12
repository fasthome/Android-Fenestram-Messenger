package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class PersonalityViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
) : BaseViewModel<PersonalityState, PersonalityEvent>(router, requestParams) {

    override fun createInitialState(): PersonalityState {
        return PersonalityState(null, false)
    }

    fun checkPersonalData() {
        //TODO Выход с заполненными данными
        //exitWithResult()
    }

    fun skipPersonalData() {
        exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
    }

    fun fillingPersonalData(data: String, hasFocus: Boolean, key: PersonalityFragment.EditTextKey) {
        if (!hasFocus && data.isNotEmpty())
            updateState { PersonalityState(key, true) }
        else
            updateState { PersonalityState(key, false) }
    }

    fun fillingBirthdate(data: String, hasFocus: Boolean, key: PersonalityFragment.EditTextKey) {
        if (!hasFocus && data.length == 10)
            updateState { PersonalityState(key, true) }
        else
            updateState { PersonalityState(key, false) }

    }
}