package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

sealed class SendMessageResult {
    object Success : SendMessageResult()
}