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

    @SerialName("replyMessage")
    val replyMessage: MessageFullResponse?,

    @SerialName("reply_message_id")
    val replyMessageId: Long?,

    @SerialName("access")
    val access: List<Long>,

    @SerialName("accessChats")
    val accessChats: List<Long>,

    @SerialName("is_edited")
    val isEdited: Boolean,

    @SerialName("message_status")
    val status: String,

    @SerialName("text")
    val text: String,

    @SerialName("chat_id")
    val chatId: String?,

    @SerialName("author_id")
    val authorId: Long?,

    @SerialName("message_type")
    val type: String,

    @SerialName("created_at")
    val date: String
)