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

    @SerialName("reply_message_id")
    val replyMessageId: Long? = null,

    @SerialName("access")
    val access: List<Long>? = null,

    @SerialName("accessChats")
    val accessChats: List<Long>? = null,

    @SerialName("is_edited")
    val isEdited: Boolean,

    @SerialName("message_status")
    val status: String,

    @SerialName("replyMessage")
    val replyMessage: MessageResponse? = null,

    @SerialName("text")
    val text: String?,

    @SerialName("chat_id")
    val chatId: String? = null,

    @SerialName("author_id")
    val authorId: Long? = null,

    @SerialName("message_type")
    val type: String,

    @SerialName("created_at")
    val date: String,

    @SerialName("users_have_read")
    val usersHaveRead: List<Long?>?,

    @SerialName("reactions")
    val reactions: Map<String, List<UserResponse>>? = null,

    @SerialName("forwarded_messages")
    val forwardedMessages: List<MessageResponse>?,

    @SerialName("content")
    val content: List<ContentResponse>
)