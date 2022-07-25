package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.coroutines.launch

class CodeViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val authInteractor: AuthInteractor,
    private val params: CodeNavigationContract.Params,
    private val permissionInterface: PermissionInterface,
) : BaseViewModel<CodeState, CodeEvent>(router, requestParams) {

    private val personalityLauncher = registerScreen(PersonalityNavigationContract) { result ->
        exitWithResult(CodeNavigationContract.createResult(result))
    }

    init {
        viewModelScope.launch {
            permissionInterface.request(Manifest.permission.RECEIVE_SMS)
        }
    }

    override fun createInitialState(): CodeState {
        return CodeState(filled = false, error = false, autoFilling = null)
    }

    fun checkCode(code: String) {
        viewModelScope.launch {
            when (val loginResult = authInteractor.login(params.phoneNumber, code)) {
                is CallResult.Success -> personalityLauncher.launch(NoParams)
                is CallResult.Error -> {
                    when (loginResult.error) {
                        is WrongServerResponseException -> updateState {
                            CodeState(
                                error = true,
                                filled = false,
                                autoFilling = null
                            )
                        }
                        else -> onError(ShowErrorType.Popup, loginResult.error)
                    }
                }
            }
        }
    }

    fun autoFillCode(code: String) {
        updateState { CodeState(filled = true, error = false, code) }
    }

    fun resendCode() {
        viewModelScope.launch {
            authInteractor.sendCode(params.phoneNumber).successOrSendError()
        }
    }

    fun overWriteCode(code: String) {
        updateState { state ->
            state.copy(filled = code.length == 4, error = false)
        }
    }
}
