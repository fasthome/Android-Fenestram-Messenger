package io.fasthome.fenestram_messenger.messenger_api.entity

data class SendMessageResult(
    val localId: String,
    val id: Long,
    val userName: String?
    )