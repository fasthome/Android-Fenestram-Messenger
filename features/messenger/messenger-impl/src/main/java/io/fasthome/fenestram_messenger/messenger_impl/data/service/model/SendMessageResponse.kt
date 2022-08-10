package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SendMessageResponse(
    @SerialName("message")
    val message: Message?
) {
    @Serializable
    class Message(
        @SerialName("text")
        val text: String?,

        @SerialName("message_type")
        val type: String?,

        @SerialName("initiator_id")
        val initiatorId: Int?,

        @SerialName("id")
        val id: Int?,

        @SerialName("created_at")
        val date: String?,

        @SerialName("initiator")
        val initiator: Initiator?
    )
}

