package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UserDetail
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

    private val resendCodeTimer = ResendCodeTimer(duration.toLong(), step.toLong()) {
        updateState { CodeState.ChangeTime(this) }
    }

    init {
        resendCodeTimer.start()
        viewModelScope.launch {
            permissionInterface.request(Manifest.permission.RECEIVE_SMS)
        }
    }

    override fun createInitialState(): CodeState {
        return CodeState.GlobalState(filled = false, error = false, autoFilling = null)
    }

    fun checkCode(code: String) {
        viewModelScope.launch {
            when(val loginResult = authInteractor.login(params.phoneNumber, code).successOrSendError()){
                is LoginResult.Success->{
                    personalityLauncher.launch(PersonalityNavigationContract.Params(
                        userDetail = loginResult.userDetail
                    ))
                }
            }
        }
    }

    fun autoFillCode(code: String) {
        updateState { CodeState.GlobalState(filled = true, error = false, code) }
    }

    fun resendCode() {
        resendCodeTimer.start()
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
            updateState { CodeState.GlobalState(filled = true, error = false, autoFilling = null) }
        else
            updateState { CodeState.GlobalState(filled = false, error = false, autoFilling = null) }
    }

    companion object {
        const val duration = 15000
        const val step = 1
    }
}
