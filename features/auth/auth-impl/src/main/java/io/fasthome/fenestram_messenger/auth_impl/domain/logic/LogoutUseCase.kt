package io.fasthome.fenestram_messenger.auth_impl.domain.logic

import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.onSuccess

class LogoutUseCase(
    private val clearUserDataUseCase: ClearUserDataUseCase,
) {
    suspend fun invoke(): CallResult<Unit> {
        val logoutResult = CallResult.Success(Unit)
        return logoutResult
            .onSuccess {
                clearUserDataUseCase.invoke()
            }
    }
}