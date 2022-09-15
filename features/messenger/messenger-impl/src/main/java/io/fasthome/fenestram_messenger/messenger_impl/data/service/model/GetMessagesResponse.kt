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
    val total: Int
)