package io.fasthome.fenestram_messenger.contacts_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendContactsRequest(
    @SerialName("contacts")
    val contacts: List<ContactsRequest>,
)
