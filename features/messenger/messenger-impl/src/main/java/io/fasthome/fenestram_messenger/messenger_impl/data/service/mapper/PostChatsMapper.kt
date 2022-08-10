package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.PostChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.network.model.BaseResponse

object PostChatsMapper {

    fun responseToPostChatsResult(response: BaseResponse<PostChatsResponse>): PostChatsResult {
        response.data?.let {
            return PostChatsResult.Success(chatId = it.id)
        }
        throw Exception()
    }

}