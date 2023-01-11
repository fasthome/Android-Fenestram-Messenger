package io.fasthome.fenestram_messenger.contacts_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UpdateContactNameRequest(
    @SerialName("name")
    val name: String,

    @SerialName("phone")
    val phone: String,
)