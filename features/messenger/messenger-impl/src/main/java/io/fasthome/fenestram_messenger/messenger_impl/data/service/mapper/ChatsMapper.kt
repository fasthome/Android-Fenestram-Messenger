package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.network.util.NetworkMapperUtil
import java.time.ZoneId
import java.time.ZonedDateTime

class ChatsMapper(private val profileImageUrlConverter: ProfileImageUrlConverter) {

    fun toMessage(messageResponse: MessageResponseWithChatId): Message = Message(
        id = messageResponse.id,
        text = messageResponse.text,
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
        replyMessage = null // TODO: ADD REPLY MESSEGE WHEN YURII FIX SOCKET
    )
}