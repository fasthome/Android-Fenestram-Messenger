package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocketDeletedChat(
    @SerialName("data")
    val data: SocketDeletedResponse
) {
    @Serializable
    data class SocketDeletedResponse(
        @SerialName("chat_id")
        val chatId:Long
    )
}

