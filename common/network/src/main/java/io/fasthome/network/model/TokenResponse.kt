package io.fasthome.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TokenResponse(
    @SerialName("user_id")
    val userId: Long? = null,

    @SerialName("access_token")
    val accessToken: String? = null,

    @SerialName("refresh_token")
    val refreshToken: String? = null,

    @SerialName("token_type")
    val tokenType: String? = null,

    @SerialName("expires_in")
    val expiresIn: Long? = null,
)