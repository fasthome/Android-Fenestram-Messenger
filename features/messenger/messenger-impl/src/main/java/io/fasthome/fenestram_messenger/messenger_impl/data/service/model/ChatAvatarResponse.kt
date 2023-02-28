package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatAvatarResponse(

    @SerialName("chatId")
    val chatId: Long,

    @SerialName("avatar")
    val avatarUrl: String,
)