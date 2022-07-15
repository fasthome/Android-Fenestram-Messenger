package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class PersonalityViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val profileInteractor: ProfileInteractor
) : BaseViewModel<PersonalityState, PersonalityEvent>(router, requestParams) {

    override fun createInitialState(): PersonalityState {
        return PersonalityState(null, false)
    }

    fun checkPersonalData(personalData: PersonalData) {
        viewModelScope.launch {
            profileInteractor.sendPersonalData(personalData) {
                if (this)
                    exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
                else
                    sendEvent(PersonalityEvent.IndefiniteError)
            }
        }
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

    fun fillingEmail(data: String, hasFocus: Boolean, key: PersonalityFragment.EditTextKey) {
        if (!hasFocus && Patterns.EMAIL_ADDRESS.matcher(data).matches())
            updateState { PersonalityState(key, true) }
        else
            updateState { PersonalityState(key, false) }
    }

}