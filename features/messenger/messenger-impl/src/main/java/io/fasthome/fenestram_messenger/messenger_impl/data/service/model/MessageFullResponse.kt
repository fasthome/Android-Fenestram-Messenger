package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageFullResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("initiator_id")
    val initiatorId: Long,

    @SerialName("user")
    val initiator: Initiator?,

    @SerialName("replyMessage")
    val replyMessage: MessageResponse?,

    @SerialName("is_edited")
    val isEdited: Boolean,

    @SerialName("text")
    val text: String,

    @SerialName("author")
    val author: Unit?,

    @SerialName("message_type")
    val type: String,

    @SerialName("created_at")
    val date: String
)