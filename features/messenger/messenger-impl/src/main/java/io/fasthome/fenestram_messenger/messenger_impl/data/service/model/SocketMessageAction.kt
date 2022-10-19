package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SocketMessageAction(
    @SerialName("message")
    val message: MessageActionResponse
)