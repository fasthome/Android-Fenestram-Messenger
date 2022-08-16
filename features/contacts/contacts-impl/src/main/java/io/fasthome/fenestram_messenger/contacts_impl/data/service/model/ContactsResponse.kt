/**
 * Created by Dmitry Popov on 16.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ContactsResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String?,
    @SerialName("phone")
    val phone: String,
    @SerialName("user")
    val user: User?
)

@Serializable
class User(
    @SerialName("id")
    val id: Long,
)