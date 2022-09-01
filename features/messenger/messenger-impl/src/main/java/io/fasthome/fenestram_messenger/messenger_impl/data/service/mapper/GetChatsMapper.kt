package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.network.util.NetworkMapperUtil
import java.time.ZoneId
import java.time.ZonedDateTime

class GetChatsMapper(private val profileImageUrlConverter: ProfileImageUrlConverter) {

    fun responseToGetChatsResult(response: GetChatsResponse, selfUserId: Long): GetChatsResult {
        response.data?.let { list ->
            return GetChatsResult.Success(
                chats = list.map { chat ->
                    val lastMessage = chat.message?.lastOrNull()
                    Chat(
                        id = chat.id,
                        users = chat.users,
                        name = chat.name ?: "",
                        messages = listOfNotNull(lastMessage?.let {
                            Message(
                                id = lastMessage.id,
                                text = lastMessage.text,
                                userSenderId = lastMessage.initiatorId,
                                messageType = lastMessage.type,
                                date = lastMessage.date.let(NetworkMapperUtil::parseZonedDateTime),
                                initiator = null
                            )
                        }
                        ),
                        time = getZonedTime(lastMessage?.date) ?: chat.date.let(
                            NetworkMapperUtil::parseZonedDateTime
                        ).withZoneSameInstant(ZoneId.systemDefault()),
                        avatar = profileImageUrlConverter.convert(chat.avatar),
                        isGroup = chat.isGroup
                    )
                }
            )
        }
        throw Exception()
    }

    fun responseToGetMessagesByChat(response: List<MessageResponse>): List<Message> {
        return response.map {
            Message(
                id = it.id,
                text = it.text,
                userSenderId = it.initiatorId,
                messageType = it.type,
                date = getZonedTime(it.date),
                initiator = it.initiator?.let { user->
                    User(
                        id = user.id,
                        phone = user.phone ?: "",
                        name = user.name ?: "",
                        nickname = user.nickname ?: "",
                        email = user.email ?: "",
                        birth = user.birth ?: "",
                        avatar = profileImageUrlConverter.convert(user.avatar),
                        isOnline = user.isOnline,
                        lastActive = ZonedDateTime.now()
                    )
                }
            )
        }
    }

    fun getZonedTime(date: String?): ZonedDateTime? {
        return date?.let(NetworkMapperUtil::parseZonedDateTime)
            ?.withZoneSameInstant(ZoneId.systemDefault())
    }
}

