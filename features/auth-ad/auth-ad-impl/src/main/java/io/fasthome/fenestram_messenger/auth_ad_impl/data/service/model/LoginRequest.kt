package io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LoginRequest(
    @SerialName("login")
    val login: String,

    @SerialName("password")
    val password: String
)