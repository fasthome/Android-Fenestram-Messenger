package io.fasthome.fenestram_messenger.auth_ad_impl.data.service.mapper

import io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model.LoginResponse
import io.fasthome.fenestram_messenger.auth_ad_impl.domain.entity.LoginResult
import io.fasthome.network.model.BaseResponse

object LoginMapper {

    fun responseToLoginResult(response: BaseResponse<LoginResponse>): LoginResult {
        response.data?.let {
            return LoginResult.Error(it.isLoginWrong, it.isPasswordWrong)
        }
        throw Exception()
    }
}