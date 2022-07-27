/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.domain.entity

import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken

sealed class LoginResult {
    /**
     * Пользователь успешно авторизовался
     */
    data class Success(
        val accessToken: AccessToken,
        val refreshToken: RefreshToken,
        val userId: String,
    ) : LoginResult()

    /**
     * Пользователь ввел неверный код из СМС или пароль
     */
    object WrongCode : LoginResult()

    /**
     * Пользователь несколько раз ввел неправильно код из СМС или пароль, сервер закрывает сессию
     */
    object SessionClosed : LoginResult()

    /**
     * У пользователя нет соединения с интернетом
     */
    object ConnectionError : LoginResult()
}