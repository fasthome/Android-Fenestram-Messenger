package io.fasthome.fenestram_messenger.push_impl.domain.entity

data class NotificationData(
    val chatId: Int?,
    val chatName: String,
    val chatAvatar: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val text: String,
    val isGroup: Boolean
)