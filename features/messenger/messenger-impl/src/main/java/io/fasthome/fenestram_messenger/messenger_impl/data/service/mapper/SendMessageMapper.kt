package io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper

import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SendMessageResponse

object SendMessageMapper {

    fun responseToSendMessageResult(
        response: SendMessageResponse,
        localId: String
    ): SendMessageResult {
        response.message?.let {
            return SendMessageResult(localId)
        }
        throw Exception()
    }
}