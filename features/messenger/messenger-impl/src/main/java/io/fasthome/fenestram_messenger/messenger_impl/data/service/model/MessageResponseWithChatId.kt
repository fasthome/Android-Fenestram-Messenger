package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageResponseWithChatId(
    @SerialName("id")
    val id: Long,

    @SerialName("initiator_id")
    val initiatorId: Long,

    @SerialName("user")
    val initiator: Initiator?,

    @SerialName("text")
    val text: String,

    @SerialName("chat_id")
    val chatId: String?,

    @SerialName("message_type")
    val type: String,

    @SerialName("created_at")
    val date: String
)