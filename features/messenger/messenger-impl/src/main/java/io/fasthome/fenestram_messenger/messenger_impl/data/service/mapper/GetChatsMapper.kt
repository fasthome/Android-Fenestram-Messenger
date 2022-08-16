package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.User
import io.fasthome.network.util.NetworkMapperUtil
import java.time.ZoneId

object GetChatsMapper {

    fun responseToGetChatsResult(response: GetChatsResponse, selfUserId: Long): GetChatsResult {
        response.data?.let { list ->
            return GetChatsResult.Success(
                chats = list.map { chat ->
                    val lastMessage = chat.message?.lastOrNull()
                    Chat(
                        id = chat.id,
                        users = chat.users.map {
                            User(it)
                        },
                        name = chat.name ?: "",
                        messages = listOfNotNull(lastMessage?.let {
                            Message(
                                id = lastMessage.id,
                                text = lastMessage.text,
                                userSenderId = lastMessage.initiator,
                                messageType = lastMessage.type,
                                date = lastMessage.date.let(NetworkMapperUtil::parseZonedDateTime)
                            )
                        }
                        ),
                        time = lastMessage?.date?.let(NetworkMapperUtil::parseZonedDateTime)
                            ?.withZoneSameInstant(ZoneId.systemDefault()) ?: chat.date.let(
                            NetworkMapperUtil::parseZonedDateTime
                        ).withZoneSameInstant(ZoneId.systemDefault())
                    )
                }
            )
        }
        throw Exception()
    }
}

