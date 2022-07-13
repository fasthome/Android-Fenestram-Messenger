package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
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

    private val checkCodeLauncher = registerScreen(CodeNavigationContract){result->
        exitWithResult(WelcomeNavigationContract.createResult(result))
    }

    override fun createInitialState(): WelcomeState {
        return WelcomeState(false)
    }

    fun checkPhoneNumber(phoneNumber: String) {
        if (phoneNumber.isNotEmpty() && phoneNumber.length == 10){
            viewModelScope.launch {
                //authInteractor.sendCode("+7$phoneNumber")
            }
            checkCodeLauncher.launch(CodeNavigationContract.Params(phoneNumber))

        }
        else
           updateState { WelcomeState(error = true) }
    }

    fun overWritePhoneNumber() {
        updateState { WelcomeState(error = false) }
    }

}