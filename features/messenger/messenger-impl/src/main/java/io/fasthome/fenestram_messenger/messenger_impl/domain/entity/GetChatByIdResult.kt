package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse

data class GetChatByIdResult(
    val chatUsers : List<User>
)