package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class BadgeResponse(
    @SerialName("total_pending")
    val totalPending: Long,
)