package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatChanges
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.network.util.NetworkMapperUtil
import java.time.ZoneId
import java.time.ZonedDateTime

class ChatsMapper(private val profileImageUrlConverter: StorageUrlConverter) {

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
            GetChatsMapper(profileImageUrlConverter).responseToMessage(it)
        },
        usersHaveRead = messageResponse.usersHaveRead,
        forwardedMessages = messageResponse.forwardedMessages?.let {
            it.map { mess ->
                GetChatsMapper(profileImageUrlConverter).responseToMessage(mess)
            }
        },
        content = messageResponse.content,
        reactions = mapReactions(messageResponse.reactions)
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
        val chatUsers = chatChangesResponse.updatedValues!!.chatUsers?.map { userResponse ->
            User(
                id = userResponse.id,
                phone = userResponse.phone,
                name = userResponse.name ?: "",
                nickname = userResponse.nickname ?: "",
                contactName = userResponse.contactName,
                email = userResponse.email ?: "",
                birth = userResponse.birth ?: "",
                avatar = profileImageUrlConverter.convert(userResponse.avatar),
                isOnline = userResponse.isOnline ?: false,
                lastActive = ZonedDateTime.now()
            )
        }
        return ChatChanges(
            users = chatUsers
        )
    }

    fun toMessageReactions(reactionsResponse: ReactionsResponse): MessageReactions {
        return MessageReactions(
            messageId = reactionsResponse.messageId,
            reactions = mapReactions(reactionsResponse.reactions)
        )
    }

    fun mapReactions(reactions: Map<String, List<UserResponse>>?): Map<String, List<User>> {
        return reactions?.filter { it.value.isNotEmpty() }
            ?.mapKeys { "${it.key};" }
            ?.mapValues {
                it.value.map { userResponse ->
                    User(id = userResponse.id, avatar = profileImageUrlConverter.convert(userResponse.avatar))
                }
            }
            ?: emptyMap()
    }

    companion object {
        const val TYPING_MESSAGE_STATUS = "typingMessage"
    }
}
