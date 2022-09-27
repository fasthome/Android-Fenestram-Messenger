/**
 * Created by Dmitry Popov on 11.07.2022.
 */
package io.fasthome.fenestram_messenger.auth_impl.data.service

import io.fasthome.fenestram_messenger.auth_impl.data.service.mapper.UsersMapper
import io.fasthome.fenestram_messenger.auth_impl.data.service.model.UsersResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UsersResult
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse

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