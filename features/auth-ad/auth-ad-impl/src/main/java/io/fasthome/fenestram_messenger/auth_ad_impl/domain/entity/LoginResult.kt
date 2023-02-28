package io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity

sealed class LoginResult {

    data class Error(val isLoginWrong: Boolean, val isPasswordWrong: Boolean) : LoginResult()

    object Success : LoginResult()
}