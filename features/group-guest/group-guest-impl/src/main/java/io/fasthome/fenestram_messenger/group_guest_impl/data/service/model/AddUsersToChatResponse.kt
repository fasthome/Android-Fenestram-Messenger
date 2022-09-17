package io.fasthome.fenestram_messenger.group_guest_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AddUsersToChatResponse(
    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String?,

    @SerialName("created_at")
    val createDate: String?,

    @SerialName("updated_at")
    val updateDate: String?,

    @SerialName("users")
    val users: List<Long?>?,

    @SerialName("avatar")
    val avatar: String?,

    @SerialName("is_group")
    val group: Boolean?,

    @SerialName("chatUsers")
    val chatUsers: List<User>?
)

@Serializable
class User(

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