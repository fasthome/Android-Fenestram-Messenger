package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.PersonalityNavigationContract
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class CodeViewModel(
    router: ContractRouter,
    requestParams: RequestParams
) : BaseViewModel<CodeState, CodeEvent>(router, requestParams) {

    override fun createInitialState(): CodeState {
        return CodeState(filled = false, error = false)
    }

    fun checkCode(code: String) {
        val rightCode = "12345"
        //TODO ПРОВЕРКА КОДА
        if (code == rightCode)
            registerScreen(PersonalityNavigationContract).launch(NoParams)
        else
            updateState { CodeState(filled = false, error = true) }
    }

    fun overWriteCode(code: String) {
        if (code.length == 5)
            updateState { CodeState(filled = true, error = false) }
        else
            updateState { CodeState(filled = false, error = false) }
    }
}
