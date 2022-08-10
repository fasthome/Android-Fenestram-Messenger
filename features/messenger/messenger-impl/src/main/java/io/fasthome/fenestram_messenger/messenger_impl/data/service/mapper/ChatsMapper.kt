package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message

object ChatsMapper {

    fun MessageResponse.toMessage() : Message = Message(
        id = id,
        text = text,
        userSenderId = initiator,
        messageType = type,
        createdAt = date
    )
}