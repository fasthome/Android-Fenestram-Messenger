package io.fasthome.fenestram_messenger.auth_ad_impl.data.service.mapper

import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model.UsersResponse
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.User
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.UsersResult
import io.fasthome.network.model.BaseResponse

object UsersMapper {

    fun responseToUserResult(usersResponse: BaseResponse<UsersResponse>): UsersResult {
        usersResponse.data?.let { response ->
            return UsersResult.Success(
                users = listOf(
                    User(
                        response.id ?: throw Exception(),
                        response.name ?: throw Exception(),
                        response.email ?: throw Exception(),
                        response.login ?: throw Exception()
                    )
                )
            )
        }
        throw Exception()
    }

    fun responseToUsersResult(usersResponse: BaseResponse<List<UsersResponse>>): UsersResult {
        usersResponse.data?.let { response ->
            return UsersResult.Success(
                users = response.map {
                    User(
                        it.id ?: throw Exception(),
                        it.name ?: throw Exception(),
                        it.email ?: throw Exception(),
                        it.login ?: throw Exception()
                    )
                }
            )
        }
        throw Exception()
    }
}
