package io.fasthome.fenestram_messenger.profile_guest_impl.data.service.mapper

import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.DeleteChatResponse
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.DeleteChatResult
import io.fasthome.network.model.BaseResponse

object DeleteMapper {
    fun responseToDeleteChatResult(response: BaseResponse<DeleteChatResponse>): DeleteChatResult {
        response.data?.let {
            return DeleteChatResult.Success
        }
        throw Exception()
    }
}