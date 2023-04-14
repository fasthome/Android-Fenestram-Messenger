package io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LoginRequest(
    @SerialName("grant_type")
    val grantType: String,

    @SerialName("client_id")
    val clientId: Long,

    @SerialName("client_secret")
    val clientSecret: String,

    @SerialName("username")
    val username: String,

    @SerialName("password")
    val password: String,

    @SerialName("scope")
    val scope: String,
)