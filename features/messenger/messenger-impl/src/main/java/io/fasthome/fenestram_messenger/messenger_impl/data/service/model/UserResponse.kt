/**
 * Created by Dmitry Popov on 08.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserResponse(

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

    @SerialName("is_online")
    val isOnline: Boolean?,

    @SerialName("last_active")
    val lastActive: String?
)