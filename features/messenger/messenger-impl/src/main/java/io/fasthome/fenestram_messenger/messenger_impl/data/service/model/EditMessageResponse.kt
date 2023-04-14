package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EditMessageResponse(
    @SerialName("message")
    val message: MessageFullResponse?,
)
