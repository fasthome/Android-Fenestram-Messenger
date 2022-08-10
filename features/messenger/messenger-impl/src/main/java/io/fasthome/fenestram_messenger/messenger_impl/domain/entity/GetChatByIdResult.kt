package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse

sealed class GetChatByIdResult {
    data class Success(val messages: List<MessageResponse?>?) : GetChatByIdResult()
}