package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteMessageRequest(
    val fromAll: Boolean,
    val messages: List<String>
)