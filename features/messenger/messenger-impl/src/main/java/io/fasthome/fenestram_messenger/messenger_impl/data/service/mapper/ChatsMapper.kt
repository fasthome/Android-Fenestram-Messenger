package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.network.util.NetworkMapperUtil
import java.time.ZoneId

object ChatsMapper {

    fun MessageResponse.toMessage() : Message = Message(
        id = id,
        text = text,
        userSenderId = initiator,
        messageType = type,
        date = date.let(NetworkMapperUtil::parseZonedDateTime).withZoneSameInstant(ZoneId.systemDefault())
    )
}