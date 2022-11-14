package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageStatusResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("chatId")
    val chatId: Long?,

    @SerialName("initiator_id")
    val initiatorId: Long,

    @SerialName("author_id")
    val authorId: Long?,

    @SerialName("reply_message_id")
    val replyMessageId: Long?,

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

    @SerialName("access")
    val access: List<Long>,

    @SerialName("accessChats")
    val accessChats: List<Long>,

    @SerialName("forwarded_messages")
    val forwardedMessages: List<Long>?,

    @SerialName("users_have_read")
    val usersHaveRead: List<Long>
)