package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.Manifest
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.ProfileInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.coroutines.launch

class PersonalityViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val profileInteractor: ProfileInteractor,
    private val permissionInterface: PermissionInterface,
) : BaseViewModel<PersonalityState, PersonalityEvent>(router, requestParams) {

    override fun createInitialState(): PersonalityState {
        return PersonalityState(null, false, null)
    }

    fun checkPersonalData(personalData: PersonalData) {
        viewModelScope.launch {
            profileInteractor.sendPersonalData(personalData).successOrSendError()
        }
    }

    fun skipPersonalData() {
        exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
    }

    fun fillingPersonalData(data: String, hasFocus: Boolean, key: PersonalityFragment.EditTextKey) {
        if (!hasFocus && data.isNotEmpty())
            updateState { PersonalityState(key, true, null) }
        else
            updateState { PersonalityState(key, false, null) }
    }

    fun fillingBirthdate(data: String, hasFocus: Boolean, key: PersonalityFragment.EditTextKey) {
        if (!hasFocus && data.length == 10)
            updateState { PersonalityState(key, true, null) }
        else
            updateState { PersonalityState(key, false, null) }
    }

    fun fillingEmail(data: String, hasFocus: Boolean, key: PersonalityFragment.EditTextKey) {
        if (!hasFocus && Patterns.EMAIL_ADDRESS.matcher(data).matches())
            updateState { PersonalityState(key, true, null) }
        else
            updateState { PersonalityState(key, false, null) }
    }

    fun requestPermissionAndLoadPhoto() {
        viewModelScope.launch {
            val permissionGranted =
                permissionInterface.request(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permissionGranted) {
                sendEvent(PersonalityEvent.LaunchGallery)
            }
        }
    }

    fun onUpdatePhoto(data: Uri?) {
        updateState { PersonalityState(null, false, data) }
    }

}