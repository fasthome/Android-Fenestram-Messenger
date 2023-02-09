package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import androidx.annotation.ColorRes

data class PermittedReactionViewItem(val permittedReaction: String)

data class ReactionsViewItem(
    val reaction: String,
    val userCount: Int,
    val avatars: List<String>,
    @ColorRes val reactionBackground: Int
)
