package io.fasthome.fenestram_messenger.push_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SendMessageRequest(
    @SerialName("text")
    val text: String?,

    @SerialName("message_type")
    val type: String?
)