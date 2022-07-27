package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.network.model.BaseResponse

object GetChatsMapper {

    fun responseToGetChatsResult(response: GetChatsResponse): GetChatsResult {
        response.data?.let {
            return GetChatsResult.Success
        }
        throw Exception()
    }
}

