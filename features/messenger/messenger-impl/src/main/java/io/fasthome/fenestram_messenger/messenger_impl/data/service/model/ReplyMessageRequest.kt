package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ReplyMessageRequest(
    @SerialName("text")
    val text: String,
    @SerialName("message_type")
    val messageType: String
)