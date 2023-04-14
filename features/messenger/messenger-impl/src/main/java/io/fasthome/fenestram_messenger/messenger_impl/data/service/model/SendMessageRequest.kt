package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SendMessageRequest(
    @SerialName("text")
    val text: String?,

    @SerialName("message_type")
    val type: String?,

    @SerialName("reply_message_id")
    val replyMessageId: Long?,

    @SerialName("author_id")
    val authorId: Long
)