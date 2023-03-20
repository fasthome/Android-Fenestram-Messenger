package io.fasthome.fenestram_messenger.auth_ad_impl.data.service.mapper

import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.core.exceptions.BadRequestException
import io.fasthome.network.model.TokenResponse
import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken

object LoginMapper {

    fun responseToLoginResult(response: TokenResponse): LoginResult {
        return if (response.userId != null && response.accessToken != null && response.refreshToken != null) {
            LoginResult.Success(
                AccessToken(response.accessToken!!),
                RefreshToken(response.refreshToken!!),
                response.userId!!
            )
        } else throw Exception()
    }

    fun badRequestToLoginResult(badRequest: BadRequestException): LoginResult {
        return when {
            badRequest.cause?.message?.contains("invalid_login") == true -> {
                LoginResult.Error(isLoginWrong = true, isPasswordWrong = false)
            }
            badRequest.cause?.message?.contains("invalid_password") == true -> {
                LoginResult.Error(isLoginWrong = false, isPasswordWrong = true)
            }
            else -> throw Exception()
        }
    }
}