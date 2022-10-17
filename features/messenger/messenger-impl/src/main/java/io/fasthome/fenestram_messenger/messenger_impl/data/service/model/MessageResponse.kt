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

    @SerialName("replyMessage")
    val replyMessage: MessageResponse?,
)