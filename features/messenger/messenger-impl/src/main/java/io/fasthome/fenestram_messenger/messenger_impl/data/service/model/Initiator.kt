package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Initiator(
    @SerialName("id")
    val id: Long,

    @SerialName("phone")
    val phone: String?,

    @SerialName("code")
    val code: String?,

    @SerialName("name")
    val name: String?,

    @SerialName("nickname")
    val nickname: String?,

    @SerialName("email")
    val email: String?,

    @SerialName("birth")
    val birth: String?,

    @SerialName("player_id")
    val playerId: String?,

    @SerialName("socket_id")
    val socketId: String?
)