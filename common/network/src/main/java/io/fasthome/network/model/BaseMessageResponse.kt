package io.fasthome.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class BaseMessageResponse(
    @SerialName("message")
    val message: String? = null,
)