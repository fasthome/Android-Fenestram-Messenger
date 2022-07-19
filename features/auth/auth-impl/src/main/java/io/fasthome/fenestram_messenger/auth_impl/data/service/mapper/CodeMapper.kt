package io.fasthome.fenestram_messenger.auth_impl.data.service.mapper

import io.fasthome.fenestram_messenger.auth_impl.data.service.model.CodeResponse
import io.fasthome.fenestram_messenger.auth_impl.domain.entity.CodeResult
import io.fasthome.network.model.BaseResponse

object CodeMapper {

    fun responseToLogInResult(response: BaseResponse<CodeResponse>): CodeResult {
        response.data?.let {
            return CodeResult.Success
        }

        throw Exception()
    }
}