package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.Message

sealed class GetChatByIdResult {
    data class Success(val messages: List<Message?>?) : GetChatByIdResult()
}