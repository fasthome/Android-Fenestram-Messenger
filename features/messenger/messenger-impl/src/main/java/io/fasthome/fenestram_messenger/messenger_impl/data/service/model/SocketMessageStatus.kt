package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SocketMessageStatus(
    @SerialName("pending_messages")
    val pendingMessages: Long,

    @SerialName("chat_id")
    val chatId: Long,

    @SerialName("messages")
    val messages: List<MessageStatusResponse?>?
)