package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.AuthInteractor
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class CodeViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val authInteractor: AuthInteractor,
    private val params: CodeNavigationContract.Params
) : BaseViewModel<CodeState, CodeEvent>(router, requestParams) {

    private val personalityLauncher = registerScreen(PersonalityNavigationContract) { result ->
        exitWithResult(CodeNavigationContract.createResult(result))
    }

    init {
        Log.d("phone", params.phoneNumber)
    }
    override fun createInitialState(): CodeState {
        return CodeState(filled = false, error = false)
    }

    fun checkCode(code: String) {
        viewModelScope.launch {
            authInteractor.login(code)
            personalityLauncher.launch(NoParams)
        }
    }

    fun overWriteCode(code: String) {
        if (code.length == 4)
            updateState { CodeState(filled = true, error = false) }
        else
            updateState { CodeState(filled = false, error = false) }
    }
}
