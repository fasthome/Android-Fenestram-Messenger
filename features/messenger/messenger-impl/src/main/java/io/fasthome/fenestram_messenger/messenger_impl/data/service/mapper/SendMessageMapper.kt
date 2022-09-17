package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SendMessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult

object SendMessageMapper {

    fun responseToSendMessageResult(response: SendMessageResponse, localId: String): SendMessageResult {
        response.message?.let {
            return SendMessageResult(localId)
        }
        throw Exception()
    }
}