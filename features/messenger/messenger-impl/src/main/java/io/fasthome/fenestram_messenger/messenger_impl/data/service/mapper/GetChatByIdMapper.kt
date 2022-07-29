package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatByIdResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.network.model.BaseResponse

object GetChatByIdMapper {
    fun responseToGetChatById(response: BaseResponse<GetChatByIdResponse>): GetChatByIdResult {
        response.data?.let {
            return GetChatByIdResult.Success(it.message)
        }
        throw Exception()
    }
}