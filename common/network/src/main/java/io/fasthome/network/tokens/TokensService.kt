package io.fasthome.network.tokens

import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.model.RefreshTokenResponse
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
            path = environment.endpoints.baseUrl + "api/v2/authorization/refresh",
            params = params,
            encodeInQuery = false,
            useBaseUrl = false
        ).requireData()
    }
}