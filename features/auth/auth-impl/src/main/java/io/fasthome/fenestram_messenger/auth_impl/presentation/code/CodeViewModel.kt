package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.core.exceptions.UnauthorizedException
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
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
            when (val loginResult =
                authInteractor.login(params.phoneNumber, code)) {
                is CallResult.Error -> {
                    onError(loginResult.error)
                }
                is CallResult.Success -> {
                    onSuccess(loginResult.data)
                }
            }
        }
    }

    fun autoFillCode(code: String) {
        updateState { CodeState.GlobalState(filled = true, error = false, code) }
    }

    fun resendCode() {
        resendCodeTimer.start()
        viewModelScope.launch {
            /**
             * Отпрвка кода на телефон
             */
            authInteractor.sendCode(params.phoneNumber)
        }
    }

    fun overWriteCode(code: String) {
        if (code.length == 4)
            updateState { CodeState.GlobalState(filled = true, error = false, autoFilling = null) }
        else
            updateState { CodeState.GlobalState(filled = false, error = false, autoFilling = null) }
    }

    private fun onSuccess(result: LoginResult) {
        personalityLauncher.launch(
            PersonalityNavigationContract.Params(
                auth = false,
                userDetail = result.userDetail
            )
        )
    }
    
    private fun onError(error: Throwable) {
        when (error) {
            is UnauthorizedException -> {
                updateState {
                    CodeState.GlobalState(
                        filled = false,
                        error = true,
                        autoFilling = null
                    )
                }
            }
        }
    }

    companion object {
        const val duration = 60000
        const val step = 1
    }
}