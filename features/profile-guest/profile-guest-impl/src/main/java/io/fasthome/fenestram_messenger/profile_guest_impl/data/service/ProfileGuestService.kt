package io.fasthome.fenestram_messenger.profile_guest_impl.data.service

import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.mapper.DeleteMapper
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.DeleteChatRequest
import io.fasthome.fenestram_messenger.profile_guest_impl.data.service.model.DeleteChatResponse
import io.fasthome.fenestram_messenger.profile_guest_impl.domain.entity.DeleteChatResult
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse

class ProfileGuestService(clientFactory: NetworkClientFactory) {
    private val client = clientFactory.create()

    suspend fun deleteChat(id: Long): DeleteChatResult {
        val response: BaseResponse<DeleteChatResponse> =
            client.runDelete<DeleteChatRequest?, BaseResponse<DeleteChatResponse>>(
                path = "api/v1/chats/$id",
                body = null
            )

        return DeleteMapper.responseToDeleteChatResult(response)
    }
}