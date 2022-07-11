package io.fasthome.network.tokens

import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.RefreshTokenResponse

class TokensService(
    networkClientFactory: NetworkClientFactory,
) {
    private val client = networkClientFactory.create()

    suspend fun callUpdateToken(refreshToken: RefreshToken): RefreshTokenResponse {
        //todo подкинуть нужные параметры
        val params = mapOf(
            "refresh_token" to refreshToken.s,
            "grant_type" to "refresh_token",
        )
        //todo подкинуть нужный путь
        return client.runSubmitForm(
            path = "/token",
            params = params,
            encodeInQuery = false
        )
    }
}