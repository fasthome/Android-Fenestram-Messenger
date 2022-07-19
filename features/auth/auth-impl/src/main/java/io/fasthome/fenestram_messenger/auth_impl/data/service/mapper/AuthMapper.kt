package io.fasthome.fenestram_messenger.auth_impl.data.service.mapper

import android.util.Log
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken

object AuthMapper {

    fun responseToLogInResult(response: BaseResponse<LoginResponse>): LoginResult {
        response.data?.let {
            return LoginResult.Success(
                accessToken = AccessToken(it.accessToken),
                refreshToken = RefreshToken("refresh"),
                userId = it.id.toString()
            )
        }

        if (response.error?.code == 401) {
            return LoginResult.WrongCode
        }

        if (response.error?.code == 408) {
            return LoginResult.SessionClosed
        }

        throw Exception()
    }
}