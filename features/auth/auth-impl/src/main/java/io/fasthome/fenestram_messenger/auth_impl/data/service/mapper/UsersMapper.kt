package io.fasthome.fenestram_messenger.auth_impl.data.service.mapper

import io.fasthome.fenestram_messenger.auth_impl.data.service.model.UsersResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.CodeResult
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.User
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UsersResult
import io.fasthome.network.model.BaseResponse

object UsersMapper {

    fun responseToUsersResult(response: BaseResponse<List<UsersResponse>>): UsersResult {
        response.data?.let { response ->
            return UsersResult.Success(
                users = response.map {
                    User(
                        id = it.id,
                        name = it.name ?: it.phone
                    )
                }
            )
        }

        throw Exception()
    }
}