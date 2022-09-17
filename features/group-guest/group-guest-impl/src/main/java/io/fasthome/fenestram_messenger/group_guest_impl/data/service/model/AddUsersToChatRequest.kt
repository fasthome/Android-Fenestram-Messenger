package io.fasthome.fenestram_messenger.group_guest_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AddUsersToChatRequest {
}

@Serializable
class GetChatByIdResponse(
    @SerialName("id")
    val id: Int?,
)