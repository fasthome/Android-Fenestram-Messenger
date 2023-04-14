package io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity

import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken

sealed class LoginResult {

    data class Error(val isLoginWrong: Boolean, val isPasswordWrong: Boolean) : LoginResult()

    data class Success(val accessToken: AccessToken, val refreshToken: RefreshToken, val userId: Long) : LoginResult()
}