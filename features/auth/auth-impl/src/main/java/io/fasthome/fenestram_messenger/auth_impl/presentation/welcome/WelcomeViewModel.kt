package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import android.util.Log
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class WelcomeViewModel(
    router : ContractRouter,
    requestParams: RequestParams
) : BaseViewModel<WelcomeState, WelcomeEvent>(router, requestParams) {

    override fun createInitialState(): WelcomeState {
        return WelcomeState.BeginWelcomeState
    }

    fun checkPhoneNumber(phoneNumber : String){
        val phone = false
        Log.d("phoneNumber", phoneNumber)
        if (phone)
            updateState { WelcomeState.CorrectPhoneNumberState }
        else
            updateState { WelcomeState.UncorrectPhoneNumberState }
    }

    fun overWritePhoneNumber(){
        updateState { WelcomeState.BeginWelcomeState }
    }

}