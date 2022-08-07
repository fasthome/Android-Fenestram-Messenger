package io.fasthome.fenestram_messenger.auth_impl.data.service.mapper

import io.fasthome.fenestram_messenger.auth_impl.domain.entity.ProfileResult
import io.fasthome.network.model.BaseResponse

object ProfileMapper {

    fun responseToLogInResult(response: BaseResponse<String>): ProfileResult {
        response.data?.let {
            return ProfileResult.Success
        }

        throw Exception()
    }
}