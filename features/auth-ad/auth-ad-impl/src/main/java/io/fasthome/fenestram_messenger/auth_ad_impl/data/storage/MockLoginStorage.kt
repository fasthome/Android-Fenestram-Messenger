package io.fasthome.fenestram_messenger.auth_ad_impl.data.storage

import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult

class MockLoginStorage {

    private val users =
        mapOf("Oleg" to "1234", "Vladimir" to "0000", "Dmitry" to "1111", "Sergey" to "2222", "Kolya" to "3333")

    fun login(login: String, password: String): LoginResult {
        return when {
            !users.containsKey(login) -> LoginResult.Error(isLoginWrong = true, isPasswordWrong = false)
            users[login] != password -> LoginResult.Error(isLoginWrong = false, isPasswordWrong = true)
            else -> LoginResult.Success
        }
    }
}