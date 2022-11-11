package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.GetChatsResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.network.util.NetworkMapperUtil
import java.time.ZoneId
import java.time.ZonedDateTime

class GetChatsMapper(private val profileImageUrlConverter: StorageUrlConverter) {

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
                            id = lastMessage[0].id,
                            text = lastMessage[0].text,
                            userSenderId = lastMessage[0].initiatorId,
                            messageType = lastMessage[0].type,
                            date = lastMessage[0].date.let(NetworkMapperUtil::parseZonedDateTime),
                            initiator = null,
                            isDate = false,
                            replyMessage = null,
                            isEdited = lastMessage[0].isEdited,
                            messageStatus = lastMessage[0].messageStatus,
                            usersHaveRead = null
                        )
                    }
                    ),
                    time = getZonedTime(chat.updatedDate)?.withZoneSameInstant(ZoneId.systemDefault()),
                    avatar = chat.avatar,
                    isGroup = chat.isGroup,
                    pendingMessages = chat.pendingMessages
                )
            }
        }
        throw Exception()
    }

    fun responseToGetMessagesByChat(response: List<MessageResponse>) = response.map {
            responseToMessage(it)
        }

    fun responseToMessage(mess : MessageResponse): Message {
        with(mess) {
            return Message(
                id = id,
                text = text,
                userSenderId = initiatorId,
                messageType = type,
                date = getZonedTime(createdDate),
                initiator = initiator?.let { user ->
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
                isDate = false,
                isEdited = isEdited,
                messageStatus = messageStatus,
                replyMessage = if (replyMessage != null) responseToMessage(replyMessage) else null,
                usersHaveRead = usersHaveRead
            )
        }
    }


    fun getZonedTime(date: String?): ZonedDateTime? {
        return date?.let(NetworkMapperUtil::parseZonedDateTime)
            ?.withZoneSameInstant(ZoneId.systemDefault())
    }
}

