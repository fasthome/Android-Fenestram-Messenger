package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PostChatsResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String?,

    @SerialName("created_at")
    val date: String?,

    @SerialName("users")
    val users: List<Int?>?,

    @SerialName("chatUsers")
    val chatUsers: List<UserResponse>?
)