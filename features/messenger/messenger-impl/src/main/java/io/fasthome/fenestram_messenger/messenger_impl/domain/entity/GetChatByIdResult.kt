package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

sealed class GetChatByIdResult {
    object Success : GetChatByIdResult()
}