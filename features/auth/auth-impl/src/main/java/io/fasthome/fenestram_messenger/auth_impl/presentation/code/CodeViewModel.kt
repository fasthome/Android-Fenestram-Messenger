package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
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
        return CodeState(filled = false, error = false)
    }

    fun checkCode(code: String) {
        viewModelScope.launch {
            when (val loginResult = authInteractor.login(params.phoneNumber, code)) {

                is CallResult.Success -> {
                    when (loginResult.data) {
                        is LoginResult.Success -> {
                            personalityLauncher.launch(NoParams)
                        }
                        is LoginResult.WrongCode -> {
                            updateState { CodeState(filled = false, error = true) }
                        }
                        is LoginResult.SessionClosed -> {
                            exitWithoutResult()
                        }
                    }
                }
                is CallResult.Error -> {
                    when (loginResult.error){
                        is InternetConnectionException -> {
                            sendEvent(CodeEvent.ConnectionError)
                        }
                        else -> {
                            sendEvent(CodeEvent.IndefiniteError)
                        }
                    }
                }
            }
        }
    }

    fun resendCode() {
//        viewModelScope.launch {
//            /**
//             * Отпрвка кода на телефон
//             */
//            authInteractor.sendCode(params.phoneNumber) {
//                when (this) {
//                    is LoginResult.SuccessSendRequest -> {}
//                    is LoginResult.ConnectionError -> {
//                        sendEvent(CodeEvent.ConnectionError)
//                    }
//                    else -> {
//                        sendEvent(CodeEvent.IndefiniteError)
//                    }
//                }
//            }
//        }
    }

    fun overWriteCode(code: String) {
        if (code.length == 4)
            updateState { CodeState(filled = true, error = false) }
        else
            updateState { CodeState(filled = false, error = false) }
    }
}
