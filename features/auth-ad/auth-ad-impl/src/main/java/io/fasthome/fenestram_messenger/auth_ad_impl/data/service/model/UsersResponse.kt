package io.fasthome.fenestram_messenger.auth_ad_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UsersResponse(
    @SerialName("id")
    val id: Long?,

    @SerialName("name")
    val name: String?,

    @SerialName("email")
    val email: String?,

    @SerialName("created_at")
    val createdAt: String?,

    @SerialName("updated_at")
    val updatedAt: String?,

    @SerialName("login")
    val login: String?,

    @SerialName("guid")
    val guid: String?,

    @SerialName("domain")
    val domain: String?,
)