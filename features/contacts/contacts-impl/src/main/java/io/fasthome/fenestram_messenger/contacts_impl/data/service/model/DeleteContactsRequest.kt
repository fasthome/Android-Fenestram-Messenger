/**
 * Created by Dmitry Popov on 16.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.data.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class DeleteContactsRequest(
    @SerialName("contacts")
    val contacts: List<Long>,
)