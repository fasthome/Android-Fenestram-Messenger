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
        return WelcomeState(false)
    }

    fun checkPhoneNumber(phoneNumber: String) {
        if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {
//            viewModelScope.launch {
//                if (authInteractor.sendCode(phoneNumber).successOrSendError() != null) {
//                    checkCodeLauncher.launch(CodeNavigationContract.Params("+7$phoneNumber"))
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