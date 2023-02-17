package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class LoginViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
//    private val authInteractor: AuthInteractor,
    private val environment: Environment
) : BaseViewModel<LoginState, LoginEvent>(router, requestParams) {

    override fun createInitialState(): LoginState {
        return LoginState(loginError = false, passwordError = false, loginButtonAlpha = 0.5f)
    }

    fun checkLoginData(login: String, password: String) {
        viewModelScope.launch {
            //TODO Domain + data

        }
    }

}