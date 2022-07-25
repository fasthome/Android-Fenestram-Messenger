package io.fasthome.fenestram_messenger.auth_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LoginResponse(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("id")
    val id: Int,

    @SerialName("phone")
    val phone: String,

    @SerialName("name")
    val name: String?,

    @SerialName("email")
    val email: String?,

    @SerialName("birth")
    val birth: String?,

    @SerialName("player_id")
    val playerId: String?,

    @SerialName("socket_id")
    val sockedId: String?
)