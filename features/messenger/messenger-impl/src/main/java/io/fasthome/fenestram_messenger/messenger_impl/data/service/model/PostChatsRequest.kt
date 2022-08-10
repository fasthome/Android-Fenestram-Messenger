package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PostChatsRequest(
    @SerialName("name")
    val name: String?,

    @SerialName("users")
    val users: List<Long?>?
)