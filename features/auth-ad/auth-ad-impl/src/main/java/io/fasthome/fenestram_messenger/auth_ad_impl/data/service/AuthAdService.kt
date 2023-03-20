package io.fasthome.fenestram_messenger.auth_ad_impl.data.service

import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.mapper.LoginMapper
import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model.LoginRequest
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.core.exceptions.BadRequestException
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.TokenResponse

class AuthAdService(
    clientFactory: NetworkClientFactory
) {
    private val client = clientFactory.create()

    suspend fun login(username: String, password: String): LoginResult {
        return try {
            val response: TokenResponse = client.runPost(
                useBaseUrl = false,
                path = "http://dev.hoolichat.ru/auth/oauth/token",
                body = LoginRequest(
                    grantType = "password",
                    clientId = 2,
                    clientSecret = "uow1Q2IGfBu8Q1z0DfcSz1vVvkmhT4Vdn7CVW1ga",
                    username = username,
                    password = password,
                    scope = ""
                ),
            )
            LoginMapper.responseToLoginResult(response)
        } catch (exception: BadRequestException) {
            LoginMapper.badRequestToLoginResult(exception)
        }
    }
}