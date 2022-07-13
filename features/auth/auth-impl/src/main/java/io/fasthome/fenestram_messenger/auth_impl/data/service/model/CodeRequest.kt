package io.fasthome.fenestram_messenger.auth_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CodeRequest(
    @SerialName("phone")
    val phone: String
)