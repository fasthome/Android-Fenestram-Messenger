package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.util.CallResult

class ForceLogoutUseCase(
    private val clearUserDataUseCase: ClearUserDataUseCase,
) {
    suspend fun invoke(): CallResult<Unit> {
        clearUserDataUseCase.invoke()
        return CallResult.Success(Unit)
    }
}