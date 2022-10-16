package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.Serializable

@Serializable
class MessageActionResponse(
    @Serializable
    val user: UserResponse?,
    @Serializable
    val action: String?
)