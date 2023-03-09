package io.fasthome.network.tokens

import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.fenestram_messenger.core.exceptions.WrongServerResponseException
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.model.RefreshTokenAdRequest
import io.fasthome.network.model.RefreshTokenResponse
import io.fasthome.network.model.TokenResponse
import io.fasthome.network.util.requireData

class TokensService(
    networkClientFactory: NetworkClientFactory,
    private val environment: Environment
) {
    private val client = networkClientFactory.create()

    suspend fun callUpdateToken(accessToken: AccessToken, refreshToken: RefreshToken): RefreshTokenResponse {
        val params = mapOf(
            "access_token" to accessToken.s,
            "refresh_token" to refreshToken.s,
        )
        return client.runSubmitForm<BaseResponse<RefreshTokenResponse>>(
            path = "authorization/refresh",
            params = params,
            encodeInQuery = false,
        ).requireData()
    }

    suspend fun callUpdateTokenAd(refreshToken: RefreshToken): RefreshTokenResponse {
        val response: TokenResponse = client.runPost(
            useBaseUrl = false,
            path = "http://dev.hoolichat.ru/auth/oauth/token",
            body = RefreshTokenAdRequest(
                grantType = "refresh_token",
                clientId = 2,
                clientSecret = "uow1Q2IGfBu8Q1z0DfcSz1vVvkmhT4Vdn7CVW1ga",
                scope = "",
                refreshToken = refreshToken.s
            ),
        )

        return when {
            response.accessToken != null && response.refreshToken != null -> RefreshTokenResponse(
                response.accessToken,
                response.refreshToken
            )
            else -> throw WrongServerResponseException()
        }
    }
}