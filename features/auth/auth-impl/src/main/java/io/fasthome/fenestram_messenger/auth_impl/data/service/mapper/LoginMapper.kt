package io.fasthome.fenestram_messenger.auth_impl.data.service.mapper

import io.fasthome.fenestram_messenger.auth_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.LoginResult
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.UserDetail
import io.fasthome.fenestram_messenger.core.environment.Environment
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.tokens.AccessToken
import io.fasthome.network.tokens.RefreshToken

class LoginMapper(
    private val environment: Environment
) {

    fun responseToLogInResult(response: BaseResponse<LoginResponse>): LoginResult {
        response.data?.let {
            return LoginResult.Success(
                accessToken = AccessToken(it.accessToken),
                refreshToken = RefreshToken(it.refreshToken),
                userDetail = UserDetail(
                    id =  it.id,
                    phone = it.phone,
                    name = it.name ?: "",
                    email = it.email ?: "",
                    nickname = it.nickname ?: "",
                    birth = it.birth ?: "",
                    profileImageUrl = environment.endpoints.apiBaseUrl.dropLast(1) + it.avatar
                )
            )
        }

        throw Exception()
    }
}