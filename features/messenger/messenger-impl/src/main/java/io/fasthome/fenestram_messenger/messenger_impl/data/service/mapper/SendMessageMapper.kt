package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SendMessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult

object SendMessageMapper {

    fun responseToSendMessageResult(response: SendMessageResponse): SendMessageResult {
        response.message?.let {
            return SendMessageResult.Success
        }
        throw Exception()
    }
}