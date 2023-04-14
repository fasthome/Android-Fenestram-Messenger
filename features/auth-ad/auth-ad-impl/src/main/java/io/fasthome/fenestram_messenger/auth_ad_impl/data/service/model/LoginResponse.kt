package io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LoginResponse(
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

    @SerialName("error")
    val error: String? = null,

    @SerialName("error_description")
    val errorDescription: String? = null,

    @SerialName("hint")
    val hint: String? = null,

    @SerialName("message")
    val message: String? = null,
)