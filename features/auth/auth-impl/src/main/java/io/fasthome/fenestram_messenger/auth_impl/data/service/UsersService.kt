/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.service

import io.fasthome.fenestram_messenger.auth_impl.data.service.mapper.CodeMapper
import io.fasthome.fenestram_messenger.auth_impl.data.service.mapper.LoginMapper
import io.fasthome.fenestram_messenger.auth_impl.data.service.mapper.UsersMapper
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.CodeRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginRequest
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.UsersResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.CodeResult
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UsersResult
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.tokens.TokensRepo

class UsersService(
    clientFactory: NetworkClientFactory,
) {
    private val client = clientFactory.create()

    suspend fun getUsers(): UsersResult {
        val response: BaseResponse<List<UsersResponse>> = client.runGet(
            path = "api/v1/users",
        )

        return UsersMapper.responseToUsersResult(response)
    }

}