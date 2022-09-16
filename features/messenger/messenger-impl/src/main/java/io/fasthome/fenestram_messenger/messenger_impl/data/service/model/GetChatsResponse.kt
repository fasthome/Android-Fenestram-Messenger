package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetChatsResponse(
    @SerialName("data")
    val data: List<Chats>?,

    @SerialName("page")
    val page: Int?,

    @SerialName("limit")
    val limit: Int?,

    @SerialName("total")
    val total: Int
) {
    @Serializable
    class Chats(
        @SerialName("id")
        val id: Long,

        @SerialName("name")
        val name: String?,

        @SerialName("avatar")
        val avatar: String?,

        @SerialName("created_at")
        val date: String,

        @SerialName("users")
        val users: List<Long>,

        @SerialName("message")
        val message: List<MessageResponseWithoutInitiator>?,

        @SerialName("is_group")
        val isGroup : Boolean
    )
}