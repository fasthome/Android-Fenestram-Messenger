package io.fasthome.fenestram_messenger.messenger_api.entity

import io.fasthome.fenestram_messenger.contacts_api.model.User

data class ChatChanges(
    val users: List<User>? = null
)

