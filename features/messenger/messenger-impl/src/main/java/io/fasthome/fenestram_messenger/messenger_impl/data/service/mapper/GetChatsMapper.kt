package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.User
import io.fasthome.network.model.BaseResponse

object GetChatsMapper {

    fun responseToGetChatsResult(response: GetChatsResponse, selfUserId: Long): GetChatsResult {
        response.data?.let { list ->
            return GetChatsResult.Success(
                chats = list.map { chat ->
                    val lastMessage = chat.message?.lastOrNull()
                    Chat(
                        id = chat.id,
                        user = User(
                            name = chat.name ?: "",
                            id = chat.users.filter {
                                it != selfUserId
                            }.random(),
                        ),
                        messages = listOf(
                            Message(
                                id = lastMessage?.id ?: 0L,
                                text = lastMessage?.text ?: "",
                                userSenderId = lastMessage?.initiator ?: 0L,
                                messageType = lastMessage?.type ?: "text",
                                createdAt = lastMessage?.date ?: ""
                            )
                        )
                    )
                }
            )
        }
        throw Exception()
    }
}

