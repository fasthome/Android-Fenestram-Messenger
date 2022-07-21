/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.presentation.logout

import io.fasthome.fenestram_messenger.auth_impl.domain.logic.ForceLogoutUseCase
import io.fasthome.fenestram_messenger.auth_impl.domain.logic.LogoutUseCase
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.NonCancellableAction
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.client.ForceLogoutManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LogoutManager(
    private val logoutUseCase: LogoutUseCase,
    private val forceLogoutUseCase: ForceLogoutUseCase,
    private val authNavigator: AuthNavigator
) : ForceLogoutManager {

    private val mutex = Mutex()

    suspend fun logout(): CallResult<Unit> {
        return mutex.withLock {
            logoutUseCase.invoke()
                .onSuccess { authNavigator.startAuth() }
        }
    }

    /**
     * Тут используется [ForceLogoutUseCase], а не [LogoutUseCase], потому что:
     * 1. force-logout вызывается в случае ошибки обновления refresh-токена и,
     * следовательно, МП уже не сможет получить рабочий access-токен, и нет смысла вызывать logout метод АПИ.
     * 2. для создания [LogoutUseCase] требуется "authorized-network-сервис" для логаут запроса на АПИ,
     * а для создания такого сервиса потребуется [ForceLogoutManager], чтобы обрабатывать ошибку обновления токена.
     * Всё это приведёт к циклической зависимости.
     */
    private val forceLogoutAction = NonCancellableAction(action = {
        forceLogoutUseCase.invoke()
        authNavigator.startAuth()
    })

    override suspend fun forceLogout() {
        if (mutex.tryLock()) {
            try {
                forceLogoutAction.invoke()
            } finally {
                mutex.unlock()
            }
        }
    }
}