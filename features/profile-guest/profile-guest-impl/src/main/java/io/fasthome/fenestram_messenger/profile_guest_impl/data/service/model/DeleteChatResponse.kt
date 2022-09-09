package io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DeleteChatResponse(
    @SerialName("raw")
    val raw: List<String>,

    @SerialName("affected")
    val affected: Int
)