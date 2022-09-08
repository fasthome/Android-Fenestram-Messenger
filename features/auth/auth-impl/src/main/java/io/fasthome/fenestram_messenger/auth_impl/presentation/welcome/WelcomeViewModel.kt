package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.CodeResult
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeNavigationContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
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
        return WelcomeState(error = false, isLoad = false)
    }

    fun checkPhoneNumber(phoneNumber: String) {
        updateState { state->
            state.copy(isLoad = true)
        }
        if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {
            viewModelScope.launch {
                when (authInteractor.sendCode("+7$phoneNumber").successOrSendError()) {
                    is CodeResult.Success -> {
                        updateState { state->
                            state.copy(isLoad = false)
                        }
                        checkCodeLauncher.launch(CodeNavigationContract.Params("+7$phoneNumber"))
                    }
                }
            }
        } else
            updateState { state->
                state.copy(error = true, isLoad = false)
            }
    }


    fun overWritePhoneNumber() {
        updateState { state->
            state.copy(error = false)
        }
    }

}