package io.fasthome.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RefreshTokenAdRequest(
    @SerialName("grant_type")
    val grantType: String,

    @SerialName("client_id")
    val clientId: Long,

    @SerialName("client_secret")
    val clientSecret: String,

    @SerialName("scope")
    val scope: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    )