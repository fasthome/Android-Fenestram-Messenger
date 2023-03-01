package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_ad_impl.R
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.logic.AuthAdInteractor
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.coroutines.launch

class LoginViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val authAdInteractor: AuthAdInteractor,
) : BaseViewModel<LoginState, LoginEvent>(router, requestParams) {

    override fun createInitialState(): LoginState {
        return LoginState(
            loginErrorMessage = PrintableText.EMPTY,
            passwordErrorMessage = PrintableText.EMPTY,
            loginButtonEnabled = false,
        )
    }

    fun checkLoginData(login: String, password: String) {
        viewModelScope.launch {
            authAdInteractor.login(login, password).withErrorHandled { result ->
                when (result) {
                    is LoginResult.Success -> {
                        authAdInteractor.onLoginResultSuccess(result)
                        Log.d("AuthAd", "Success login: $result")
                    }
                    is LoginResult.Error -> {
                        if (result.isLoginWrong) {
                            updateState { state ->
                                state.copy(loginErrorMessage = PrintableText.StringResource(R.string.auth_login_error))
                            }
                        }
                        if (result.isPasswordWrong) {
                            updateState { state ->
                                state.copy(passwordErrorMessage = PrintableText.StringResource(R.string.auth_password_error))
                            }
                        }
                    }
                }
            }
        }
    }

    fun onTextChanged(login: String, password: String, isLoginChanged: Boolean) {
        if (login.isNotEmpty() && password.isNotEmpty()) {
            updateState { state -> state.copy(loginButtonEnabled = true) }
        } else {
            updateState { state -> state.copy(loginButtonEnabled = false) }
        }
        if (isLoginChanged) updateState { state -> state.copy(loginErrorMessage = PrintableText.EMPTY) }
        else updateState { state -> state.copy(passwordErrorMessage = PrintableText.EMPTY) }
    }

    companion object {
        private const val BUTTON_ACTIVE_ALPHA = 1f
        private const val BUTTON_INACTIVE_ALPHA = 0.25f
    }
}