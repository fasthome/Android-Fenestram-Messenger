package io.fasthome.fenestram_messenger.auth_ad_impl.data.service

import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.mapper.LoginMapper
import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model.LoginRequest
import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse

class AuthAdService(
    clientFactory: NetworkClientFactory
) {
    private val client = clientFactory.create()

    suspend fun login(login: String, password: String): LoginResult {
        val response: BaseResponse<LoginResponse> = client.runPost(
            path = "oauth/token",
            body = LoginRequest(login, password),
        )

        return LoginMapper.responseToLoginResult(response)
    }
}