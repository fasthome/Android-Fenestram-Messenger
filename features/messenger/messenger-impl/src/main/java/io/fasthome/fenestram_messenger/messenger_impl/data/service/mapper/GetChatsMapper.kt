package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.User
import io.fasthome.network.model.BaseResponse

object GetChatsMapper {

    fun responseToGetChatsResult(response: GetChatsResponse): GetChatsResult {
        response.data?.let { list ->
            return GetChatsResult.Success(
                chats = list.map { chat ->
                    Chat(
                        id = chat.id,
                        user = User(chat.name ?: ""),
                        messages = chat.message?.map {
                            Message(
                                id = it.id,
                                text = it.text,
                                messageType = it.type,
                                createdAt = it.date,
                                userSenderId = it.initiator
                            )
                        } ?: listOf()
                    )
                }
            )
        }
        throw Exception()
    }
}

