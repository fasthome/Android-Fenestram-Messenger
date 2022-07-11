package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.presentation.code.CodeNavigationContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class WelcomeViewModel(
    router: ContractRouter,
    requestParams: RequestParams
) : BaseViewModel<WelcomeState, WelcomeEvent>(router, requestParams) {

    private val checkCodeLauncher = registerScreen(CodeNavigationContract){result->
        exitWithResult(WelcomeNavigationContract.createResult(result))
    }

    override fun createInitialState(): WelcomeState {
        return WelcomeState(false)
    }

    fun checkPhoneNumber(phoneNumber: String) {
        if (phoneNumber.isNotEmpty() && phoneNumber.length == 10) {
            checkCodeLauncher.launch(NoParams)
        } else
            updateState { WelcomeState(error = true) }
    }

    fun overWritePhoneNumber() {
        updateState { WelcomeState(error = false) }
    }

}