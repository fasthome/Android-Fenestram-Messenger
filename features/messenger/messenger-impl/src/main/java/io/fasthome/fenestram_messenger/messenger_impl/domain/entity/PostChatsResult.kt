package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import java.time.ZonedDateTime

data class PostChatsResult(
    val chatId: Long,
    val isOnline: Boolean,
    val lastActive: ZonedDateTime
)