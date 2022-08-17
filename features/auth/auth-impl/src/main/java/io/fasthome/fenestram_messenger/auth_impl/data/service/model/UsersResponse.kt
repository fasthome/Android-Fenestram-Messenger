package io.fasthome.fenestram_messenger.auth_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UsersResponse(
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
)