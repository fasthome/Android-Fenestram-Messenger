package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.contacts_api.model.User

data class PermittedReactionViewItem(val permittedReaction: String)

data class ReactionsViewItem(
    val reaction: String,
    val userCount: Int,
    val users: List<User>,
    val reactionBackground: Int?,
    val setBySelf: Boolean
)
