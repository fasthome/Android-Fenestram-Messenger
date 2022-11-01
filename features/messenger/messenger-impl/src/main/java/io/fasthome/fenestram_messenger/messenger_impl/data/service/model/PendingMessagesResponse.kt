package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PendingMessagesResponse(
    @SerialName("chat_id")
    val chatId: Long,

    @SerialName("pending_messages")
    val pendingMessages: Int
)