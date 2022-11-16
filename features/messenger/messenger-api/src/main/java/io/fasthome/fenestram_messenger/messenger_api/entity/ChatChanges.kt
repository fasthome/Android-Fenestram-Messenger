package io.fasthome.fenestram_messenger.messenger_api.entity

data class ChatChanges(
    val chatUsers: List<ChatUsersResult>? = null
)

data class ChatUsersResult(
    val userId: Long,
    val userName: String,
    val avatar: String
)
