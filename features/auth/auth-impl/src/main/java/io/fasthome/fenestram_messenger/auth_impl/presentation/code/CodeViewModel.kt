package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import androidx.lifecycle.viewModelScope
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
    private val authInteractor: AuthInteractor
) : BaseViewModel<CodeState, CodeEvent>(router, requestParams) {

    private val personalityLauncher = registerScreen(PersonalityNavigationContract) { result ->
        exitWithResult(CodeNavigationContract.createResult(result))
    }

    override fun createInitialState(): CodeState {
        return CodeState(filled = false, error = false)
    }

    fun checkCode(code: String) {
        val rightCode = "12345"
        //TODO ПРОВЕРКА КОДА
        if (code == rightCode) {
            viewModelScope.launch {
                authInteractor.login()
                personalityLauncher.launch(NoParams)
            }
        } else {
            updateState { CodeState(filled = false, error = true) }
        }
    }

    fun overWriteCode(code: String) {
        if (code.length == 5)
            updateState { CodeState(filled = true, error = false) }
        else
            updateState { CodeState(filled = false, error = false) }
    }
}
