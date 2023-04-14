package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ReactionsResponse(
    @SerialName("chatId")
    val chatId: Long,

    @SerialName("messageId")
    val messageId: Long,

    @SerialName("reactions")
    val reactions: Map<String, List<UserResponse>>,
)