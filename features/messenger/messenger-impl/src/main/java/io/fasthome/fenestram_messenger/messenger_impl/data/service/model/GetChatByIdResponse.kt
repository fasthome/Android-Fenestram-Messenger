package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetChatByIdResponse(
    @SerialName("id")
    val id: Int?,

    @SerialName("name")
    val name: String?,

    @SerialName("avatar")
    val avatar: String?,

    @SerialName("created_at")
    val date: String?,

    @SerialName("users")
    val users: List<Long?>?,

    @SerialName("chatUsers")
    val chatUsers: List<UserResponse>,

    @SerialName("permittedReactions")
    val permittedReactions: List<String>?
)