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
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class PersonalityViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val profileInteractor: ProfileInteractor,
    private val permissionInterface: PermissionInterface,
    private val params: PersonalityNavigationContract.Params
) : BaseViewModel<PersonalityState, PersonalityEvent>(router, requestParams) {

    override fun createInitialState(): PersonalityState {
        val detail = params.userDetail
        return PersonalityState(
            listOf(
                PersonalityState.Field(
                    key = PersonalityFragment.EditTextKey.BirthdateKey,
                    text = PrintableText.Raw(detail.birth),
                    visibility = detail.birth.isNotEmpty()
                ),
                PersonalityState.Field(
                    key = PersonalityFragment.EditTextKey.UserNameKey,
                    text = PrintableText.Raw(detail.nickname),
                    visibility = detail.nickname.isNotEmpty()
                ),
                PersonalityState.Field(
                    key = PersonalityFragment.EditTextKey.NameKey,
                    text = PrintableText.Raw(detail.name),
                    visibility = detail.name.isNotEmpty()
                ),
                PersonalityState.Field(
                    key = PersonalityFragment.EditTextKey.MailKey,
                    text = PrintableText.Raw(detail.email),
                    visibility = detail.email.isNotEmpty()
                )
            ), null
        )
    }

    fun checkPersonalData(personalData: PersonalData) {
        viewModelScope.launch {
            profileInteractor.sendPersonalData(personalData).onSuccess {
                exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
            }
        }
    }

    fun skipPersonalData() {
        exitWithResult(PersonalityNavigationContract.createResult(AuthFeature.AuthResult.Success))
    }

    fun fillData(data: String, hasFocus: Boolean, key: PersonalityFragment.EditTextKey) {
        val visibleCondition = when (key) {
            PersonalityFragment.EditTextKey.NameKey -> !hasFocus && data.isNotEmpty()
            PersonalityFragment.EditTextKey.UserNameKey -> !hasFocus && data.isNotEmpty()
            PersonalityFragment.EditTextKey.BirthdateKey -> !hasFocus && data.length == 10
            PersonalityFragment.EditTextKey.MailKey -> !hasFocus && Patterns.EMAIL_ADDRESS.matcher(data).matches()
        }
        updateState { state->
            state.copy(
                fieldsData = listOf(PersonalityState.Field(
                    key = key,
                    text = PrintableText.Raw(data),
                    visibility = visibleCondition
                ))
            )
        }
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
        updateState { PersonalityState(listOf(), data) }
    }

}