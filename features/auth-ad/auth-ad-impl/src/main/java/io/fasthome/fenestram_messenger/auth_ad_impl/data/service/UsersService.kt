package io.fasthome.fenestram_messenger.auth_ad_impl.data.service

import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.mapper.UsersMapper
import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model.UsersResponse
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.UsersResult
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseMessageResponse
import io.fasthome.network.model.BaseResponse

class UsersService(
    clientFactory: NetworkClientFactory,
) {
    private val client = clientFactory.create()

    suspend fun getUsers(): UsersResult {
        val response: BaseResponse<List<UsersResponse>> = client.runGet(
            useBaseUrl = false,
            path = "http://dev.hoolichat.ru/auth/api/v1/users"
        )

        return UsersMapper.responseToUsersResult(response)
    }

    suspend fun getUser(): UsersResult {
        val response: BaseResponse<UsersResponse> = client.runGet(
            useBaseUrl = false,
            path = "http://dev.hoolichat.ru/auth/api/v1/user",
        )

        return UsersMapper.responseToUserResult(response)
    }

    suspend fun logoutUser() {
        client.runGet<BaseResponse<BaseMessageResponse>>(
            useBaseUrl = false,
            path = "http://dev.hoolichat.ru/auth/api/v1/user/logout",
        )
    }
}