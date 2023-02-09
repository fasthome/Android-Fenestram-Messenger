package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatChanges
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageActionResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageStatusResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SocketChatChanges
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessageAction
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessageStatus
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.UserStatus
import io.fasthome.network.util.NetworkMapperUtil
import java.time.ZoneId
import java.time.ZonedDateTime

class ChatsMapper(
    private val profileImageUrlConverter: StorageUrlConverter,
    private val contentMapper: ContentMapper,
    private val getChatsMapper: GetChatsMapper
) {

    fun toMessage(messageResponse: MessageResponseWithChatId): Message = Message(
        id = messageResponse.id,
        text = messageResponse.text ?: "",
        userSenderId = messageResponse.initiatorId,
        messageType = messageResponse.type,
        date = messageResponse.date.let(NetworkMapperUtil::parseZonedDateTime)
            .withZoneSameInstant(ZoneId.systemDefault()),
        initiator = messageResponse.initiator?.let { user ->
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
        chatId = messageResponse.chatId,
        isDate = false,
        isEdited = messageResponse.isEdited,
        messageStatus = messageResponse.status,
        replyMessage = messageResponse.replyMessage?.let {
            getChatsMapper.responseToMessage(it)
        },
        usersHaveRead = messageResponse.usersHaveRead,
        forwardedMessages = messageResponse.forwardedMessages?.let {
            it.map { mess ->
                getChatsMapper.responseToMessage(mess)
            }
        },
        content = messageResponse.content.map(contentMapper::mapContentResponseToMetaInfo)
    )

    fun toMessageAction(messageActionResponse: MessageActionResponse): MessageAction {
        val userName = when {
            !messageActionResponse.user?.contactName.isNullOrEmpty() -> messageActionResponse.user?.contactName
            !messageActionResponse.user?.name.isNullOrEmpty() -> messageActionResponse.user?.name
            !messageActionResponse.user?.nickname.isNullOrEmpty() -> messageActionResponse.user?.nickname
            else -> ""
        }

        val userStatus = when {
            messageActionResponse.action == TYPING_MESSAGE_STATUS -> UserStatus.Typing
            else -> UserStatus.OnlineStatus
        }

        return MessageAction(
            userId = messageActionResponse.user?.id ?: 0,
            chatId = messageActionResponse.chatId ?: 0,
            userName = userName ?: "",
            userStatus = userStatus
        )
    }

    fun toMessageStatus(messageStatusResponse: MessageStatusResponse) = MessageStatus(
        messageId = messageStatusResponse.id,
        messageStatus = messageStatusResponse.messageStatus,
        messageType = messageStatusResponse.type,
        usersHaveRead = messageStatusResponse.usersHaveRead
    )

    fun toChatChanges(chatChangesResponse: SocketChatChanges.ChatChangesResponse): ChatChanges {
        val chatUsers = chatChangesResponse.updatedValues!!.chatUsers?.map { user ->
            User(
                id = user.id,
                phone = user.phone,
                name = user.name ?: "",
                nickname = user.nickname ?: "",
                contactName = user.contactName,
                email = user.email ?: "",
                birth = user.birth ?: "",
                avatar = profileImageUrlConverter.convert(user.avatar),
                isOnline = user.isOnline ?: false,
                lastActive = ZonedDateTime.now()
            )
        }
        return ChatChanges(
            users = chatUsers
        )
    }

    companion object {
        const val TYPING_MESSAGE_STATUS = "typingMessage"
    }
}