package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.CodeResult
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeNavigationContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
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
        return WelcomeState(country = "", error = false, isLoad = false)
    }

    fun checkPhoneNumber(phoneNumber: String, isValid: Boolean) {
        updateState { state ->
            state.copy(isLoad = true)
        }
        if (isValid) {
//            viewModelScope.launch {
//                when (val result = authInteractor.sendCode("+$phoneNumber")) {
//                    is CallResult.Error -> {
//                        onError(ShowErrorType.Popup, result.error)
//                        updateState { state ->
//                            state.copy(isLoad = false)
//                        }
//                    }
//                    is CallResult.Success -> {
//                        when(result.data){
//                            is CodeResult.Success -> {
//                                updateState { state ->
//                                    state.copy(isLoad = false)
//                                }
//                                checkCodeLauncher.launch(CodeNavigationContract.Params("+$phoneNumber"))
//                            }
//                            CodeResult.ConnectionError -> {}
//                        }
//                    }
//                }
//            }
            checkCodeLauncher.launch(CodeNavigationContract.Params("+$phoneNumber"))
        } else
            updateState { state ->
                state.copy(country = "", error = true, isLoad = false)
            }
    }

    fun updateCountry(countryName: String) {
        updateState { state ->
            state.copy(country = countryName, error = false)
        }
    }

    fun overWritePhoneNumber() {
        updateState { state ->
            state.copy(country = currentViewState.country, error = false)
        }
    }

}