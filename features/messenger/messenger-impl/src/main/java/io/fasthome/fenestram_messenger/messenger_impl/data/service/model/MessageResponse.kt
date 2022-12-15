package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("initiator_id")
    val initiatorId: Long,

    @SerialName("user")
    val initiator: Initiator?,

    @SerialName("text")
    val text: String,

    @SerialName("message_type")
    val type: String,

    @SerialName("created_at")
    val createdDate: String,

    @SerialName("is_edited")
    val isEdited: Boolean,

    @SerialName("message_status")
    val messageStatus: String,

    @SerialName("replyMessage")
    val replyMessage: MessageResponse?,

    @SerialName("totalMessages")
    val totalMessages: Long,

    @SerialName("users_have_read")
    val usersHaveRead: List<Long?>?,

    @SerialName("forwarded_messages")
    val forwardedMessages: List<MessageResponse>,

    @SerialName("author")
    val author: Initiator?,

    @SerialName("content")
    val content: List<ContentResponse>? = null,

    @SerialName("reactions")
    val reactions: MessageReactions? = null,
)