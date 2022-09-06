package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeNavigationContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class WelcomeViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val authInteractor: AuthInteractor
) : BaseViewModel<WelcomeState, WelcomeEvent>(router, requestParams) {

    private val checkCodeLauncher = registerScreen(CodeNavigationContract) { result ->
        exitWithResult(WelcomeNavigationContract.createResult(result))
    }

    override fun createInitialState(): WelcomeState {
        return WelcomeState(country = "", error = false)
    }

    fun checkPhoneNumber(phoneNumber: String, isValid: Boolean) {
        if (isValid) {
//            viewModelScope.launch {
//                if (authInteractor.sendCode(phoneNumber).successOrSendError() != null) {
//                    checkCodeLauncher.launch(CodeNavigationContract.Params("+$phoneNumber"))
//                }
//            }
            checkCodeLauncher.launch(CodeNavigationContract.Params("+$phoneNumber")) // Временная заглушка, чтобы не отпралять код каждый раз
        } else
            updateState { WelcomeState(country = "", error = true) }
    }

    fun updateCountry(countryName: String) {
        updateState { WelcomeState(country = countryName, error = false) }
    }

    fun overWritePhoneNumber() {
        updateState { WelcomeState(country = currentViewState.country, error = false) }
    }

}