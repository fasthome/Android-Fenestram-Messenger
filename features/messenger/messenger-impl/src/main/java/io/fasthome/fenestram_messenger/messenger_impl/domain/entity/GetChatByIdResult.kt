package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import io.fasthome.fenestram_messenger.contacts_api.model.User

data class GetChatByIdResult(
    val avatar: String,
    val chatName: String,
    val chatUsers: List<User>,
    val permittedReactions: List<String>
)