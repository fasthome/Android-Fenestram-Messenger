package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class PersonalityViewModel(router : ContractRouter,
                    requestParams: RequestParams
) : BaseViewModel<PersonalityState, PersonalityEvent>(router, requestParams) {

    override fun createInitialState(): PersonalityState {
        return PersonalityState.BeginCodePersonalityState
    }

    fun checkPersonalData(){
        //TODO ПРОВЕРКА ЛИЧНЫХ ДАННЫх
    }
}