package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageActionResponse(
    @SerialName("user")
    val user: UserResponse?,
    @SerialName("action")
    val action: String?,
    @SerialName("chat_id")
    val chatId: Long?
)