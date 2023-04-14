package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessagesResponse(
    @SerialName("data")
    val data: List<MessageResponse>?,

    @SerialName("page")
    val page: Int?,

    @SerialName("limit")
    val limit: Int?,

    @SerialName("total")
    val total: Int,

//    @SerialName("chat")
//    val chat: ChatResponse? = null
)

@Serializable
class ChatResponse(
    val id: Long,
    val name: String,
    @SerialName("created_at")
    val createdDate: String,
    @SerialName("updated_at")
    val updatedDate: String,
    @SerialName("users")
    val users: Long,
    @SerialName("avatar")
    val avatar: String,
    @SerialName("is_group")
    val isGroup: Boolean,
    @SerialName("listeners")
    val listeners: Unit? = null,
    val permittedReactions: List<String>? = null,
    val pendingMessages: Long? = null,
    )