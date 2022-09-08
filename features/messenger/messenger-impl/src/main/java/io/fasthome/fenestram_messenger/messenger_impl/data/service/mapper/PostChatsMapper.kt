package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.PostChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import java.time.ZonedDateTime

object PostChatsMapper {

    fun responseToPostChatsResult(response: PostChatsResponse): PostChatsResult {
        response.let {
            return PostChatsResult(chatId = it.id, isOnline = false, lastActive = ZonedDateTime.now())
        }
    }

}