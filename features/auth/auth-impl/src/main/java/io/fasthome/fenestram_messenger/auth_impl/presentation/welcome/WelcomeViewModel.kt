package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.CodeResult
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeNavigationContract
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.debug_api.DebugFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.coroutines.launch

class WelcomeViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val authInteractor: AuthInteractor,
    private val debugFeature : DebugFeature,
    private val environment : Environment
) : BaseViewModel<WelcomeState, WelcomeEvent>(router, requestParams) {

    private val checkCodeLauncher = registerScreen(CodeNavigationContract) { result ->
        exitWithResult(WelcomeNavigationContract.createResult(result))
    }
    private val debugLauncher = registerScreen(debugFeature.navigationContract)

    override fun createInitialState(): WelcomeState {
        return WelcomeState(error = false, isLoad = false, debugVisible = environment.isDebug)
    }

    fun checkPhoneNumber(phoneNumber: String, isValid: Boolean) {
        updateState { state ->
            state.copy(isLoad = true)
        }
        if (isValid) {
            viewModelScope.launch {
                when (val result = authInteractor.sendCode("+$phoneNumber")) {
                    is CallResult.Error -> {
                        onError(ShowErrorType.Popup, result.error)
                        updateState { state ->
                            state.copy(isLoad = false)
                        }
                    }
                    is CallResult.Success -> {
                        when(result.data){
                            is CodeResult.Success -> {
                                updateState { state ->
                                    state.copy(isLoad = false)
                                }
                                checkCodeLauncher.launch(CodeNavigationContract.Params("+$phoneNumber"))
                            }
                            CodeResult.ConnectionError -> {}
                        }
                    }
                }
            }
        } else
            updateState { state ->
                state.copy(error = true, isLoad = false)
            }
    }

    fun overWritePhoneNumber() {
        updateState { state ->
            state.copy(error = false)
        }
    }

    fun debugClicked() {
        debugLauncher.launch()
    }

}