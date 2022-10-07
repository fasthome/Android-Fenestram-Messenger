package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.network.util.NetworkMapperUtil
import java.time.ZoneId
import java.time.ZonedDateTime

class GetChatsMapper(private val profileImageUrlConverter: ProfileImageUrlConverter) {

    fun responseToGetChatsResult(response: GetChatsResponse): List<Chat> {
        response.data?.let { list ->
            return list.map { chat ->
                val lastMessage = chat.message
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
                            initiator = null,
                            isDate = false
                        )
                    }
                    ),
                    time = getZonedTime(chat.updatedDate)?.withZoneSameInstant(ZoneId.systemDefault()),
                    avatar = profileImageUrlConverter.convert(chat.avatar),
                    isGroup = chat.isGroup
                )
            }
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
                date = getZonedTime(it.createdDate),
                initiator = it.initiator?.let { user ->
                    User(
                        id = user.id,
                        phone = user.phone ?: "",
                        name = user.name ?: "",
                        nickname = user.nickname ?: "",
                        contactName = user.contactName,
                        email = user.email ?: "",
                        birth = user.birth ?: "",
                        avatar = profileImageUrlConverter.convert(user.avatar),
                        isOnline = user.isOnline,
                        lastActive = ZonedDateTime.now()
                    )
                },
                isDate = false
            )
        }
    }

    fun getZonedTime(date: String?): ZonedDateTime? {
        return date?.let(NetworkMapperUtil::parseZonedDateTime)
            ?.withZoneSameInstant(ZoneId.systemDefault())
    }
}

