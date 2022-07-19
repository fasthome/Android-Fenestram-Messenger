package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeEvent
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeNavigationContract
import io.fasthome.fenestram_messenger.core.exceptions.InternetConnectionException
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.coroutines.launch

class WelcomeViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val authInteractor: AuthInteractor
) : BaseViewModel<WelcomeState, WelcomeEvent>(router, requestParams) {

    private val checkCodeLauncher = registerScreen(CodeNavigationContract) { result ->
        exitWithResult(WelcomeNavigationContract.createResult(result))
    }

    override fun createInitialState(): WelcomeState {
        return WelcomeState(false)
    }

    fun checkPhoneNumber(phoneNumber: String) {
        if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {
            /**
             * Отпрвка кода на телефон
             */
//            viewModelScope.launch {
//                when (val codeResult = authInteractor.sendCode("+7$phoneNumber")) {
//                    is CallResult.Success -> {
//                        checkCodeLauncher.launch(CodeNavigationContract.Params("+7$phoneNumber"))
//                    }
//                    is CallResult.Error -> {
//                        when (codeResult.error) {
//                            is InternetConnectionException -> {
//                                sendEvent(WelcomeEvent.ConnectionError)
//                            }
//                            else -> {
//                                sendEvent(WelcomeEvent.IndefiniteError)
//                            }
//                        }
//                    }
//                }
//            }

            checkCodeLauncher.launch(CodeNavigationContract.Params("+7$phoneNumber")) // Временная заглушка, чтобы не отпралять код каждый раз
        } else
            updateState { WelcomeState(error = true) }
    }


    fun overWritePhoneNumber() {
        updateState { WelcomeState(error = false) }
    }

}