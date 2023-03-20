package io.fasthome.fenestram_messenger.messenger_impl.data.storage.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.model.ChatTable
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.model.ContentDb
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.model.MessageDb
import io.fasthome.fenestram_messenger.messenger_impl.data.storage.model.UserDb
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.util.model.MetaInfo

object ChatDbMapper {

    fun mapChatToTable(chats: List<Chat>) = chats.map { chat ->
        ChatTable(
            id = chat.id,
            name = chat.name,
            users = chat.users,
            messages = chat.messages.map { message ->
                ObjectMapper.toString(mapMessageToTable(message))
            },
            time = chat.time,
            avatar = chat.avatar,
            isGroup = chat.isGroup,
            pendingMessages = chat.pendingMessages
        )
    }

    fun mapMessageToTable(message: Message?): MessageDb? = if (message == null) null else MessageDb(
        id = message.id,
        text = message.text,
        userSenderId = message.userSenderId,
        messageType = message.messageType,
        date = message.date,
        initiator = ObjectMapper.toString(mapUserToTable(message.initiator)),
        chatId = message.chatId,
        isDate = message.isDate,
        isEdited = message.isEdited,
        messageStatus = message.messageStatus,
        replyMessage = mapMessageToTable(message.replyMessage),
        usersHaveRead = message.usersHaveRead,
        forwardedMessages = message.forwardedMessages?.mapNotNull
        {
            ObjectMapper.toString(mapMessageToTable(it))
        },
        content = message.content?.map
        { content ->
            ObjectMapper.toString(
                ContentDb(
                    name = content.name,
                    extension = content.extension,
                    size = content.size,
                    url = content.url
                )
            )
        }
    )

    fun mapTableToChat(chat: ChatTable) = Chat(
            id = chat.id,
            name = chat.name,
            users = chat.users,
            messages = chat.messages.mapNotNull { message ->
                mapTableToMessage(ObjectMapper.fromString(message))
            },
            time = chat.time,
            avatar = chat.avatar,
            isGroup = chat.isGroup,
            pendingMessages = chat.pendingMessages
        )

    fun mapTableToChat(chats: List<ChatTable>) = chats.map { chat ->
        mapTableToChat(chat)
    }.sortedByDescending { it.messages.lastOrNull()?.date }

    fun mapTableToMessage(message: MessageDb?): Message? = if (message == null) null else Message(
        id = message.id,
        text = message.text,
        userSenderId = message.userSenderId,
        messageType = message.messageType,
        date = message.date,
        initiator = mapTableToUser(ObjectMapper.fromString(message.initiator)),
        chatId = message.chatId,
        isDate = message.isDate,
        isEdited = message.isEdited,
        messageStatus = message.messageStatus,
        replyMessage = mapTableToMessage(message.replyMessage),
        usersHaveRead = message.usersHaveRead,
        forwardedMessages = message.forwardedMessages?.mapNotNull {
            mapTableToMessage(ObjectMapper.fromString(it))
        },
        content = message.content?.mapNotNull { content ->
            val contentDb = ObjectMapper.fromString<ContentDb>(content) ?: return@mapNotNull null
            MetaInfo(
                name = contentDb.name,
                extension = contentDb.extension,
                size = contentDb.size,
                url = contentDb.url
            )
        } ?: emptyList(),
        reactions = emptyMap()
    )

    fun mapUserToTable(user: User?) = if (user == null) null else UserDb(
        id = user.id,
        phone = user.phone,
        name = user.name,
        nickname = user.nickname,
        email = user.email,
        contactName = user.contactName,
        birth = user.birth,
        avatar = user.avatar,
        isOnline = user.isOnline,
        lastActive = user.lastActive,
    )

    fun mapTableToUser(user: UserDb?) = if (user == null) null else User(
        id = user.id,
        phone = user.phone,
        name = user.name,
        nickname = user.nickname,
        email = user.email,
        contactName = user.contactName,
        birth = user.birth,
        avatar = user.avatar,
        isOnline = user.isOnline,
        lastActive = user.lastActive,
    )
}