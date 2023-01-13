package io.fasthome.fenestram_messenger.auth_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LoginResponse(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("id")
    val id: Long,

    @SerialName("phone")
    val phone: String,

    @SerialName("name")
    val name: String?,

    @SerialName("nickname")
    val nickname: String?,

    @SerialName("email")
    val email: String?,

    @SerialName("birth")
    val birth: String?,

    @SerialName("avatar")
    val avatar: String?,
)