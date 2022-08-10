package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetChatByIdResponse(
    @SerialName("id")
    val id: Int?,

    @SerialName("name")
    val name: String?,

    @SerialName("created_at")
    val date: String?,

    @SerialName("users")
    val users: List<Int?>?,

    @SerialName("message")
    val message: List<MessageResponse?>?
)