package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams

class CodeViewModel(router : ContractRouter,
                    requestParams: RequestParams
) : BaseViewModel<CodeState, CodeEvent>(router, requestParams) {

    override fun createInitialState(): CodeState {
        return CodeState.BeginCodeState
    }

    fun checkCode(code : String){
        //TODO ПРОВЕРКА КОДА
        if (code == "12345")
            updateState { CodeState.CorrectCodeState }
        else
            updateState { CodeState.UncorrectCodeState }
    }

    fun overWriteCode(){
        updateState { CodeState.BeginCodeState }
    }
}
