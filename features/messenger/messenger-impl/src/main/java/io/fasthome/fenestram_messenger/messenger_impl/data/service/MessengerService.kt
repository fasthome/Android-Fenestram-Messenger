package io.fasthome.fenestram_messenger.messenger_impl.data.service

import io.fasthome.fenestram_messenger.messenger_impl.data.MessengerSocket
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.GetChatByIdMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.GetChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.PostChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.SendMessageMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse

class MessengerService(clientFactory: NetworkClientFactory, val socket: MessengerSocket) {
    private val client = clientFactory.create()

    suspend fun sendMessage(id: Int, text: String, type: String): SendMessageResult {
        val response: SendMessageResponse = client.runPost(
            path = "api/v1/chats/message/$id",
            body = SendMessageRequest(text, type)
        )

        return SendMessageMapper.responseToSendMessageResult(response)
    }

    suspend fun getChats(limit: Int, page: Int): GetChatsResult {
        val response: GetChatsResponse = client.runGet(
            path = "api/v1/chats",
            params = mapOf("limit" to limit, "page" to page)
        )

        return GetChatsMapper.responseToGetChatsResult(response)
    }

    suspend fun postChats(name: String, users: List<Int>): PostChatsResult {
        val response: BaseResponse<PostChatsResponse> = client.runPost(
            path = "api/v1/chats",
            body = PostChatsRequest(name, users)
        )

        return PostChatsMapper.responseToPostChatsResult(response)
    }

    suspend fun getChatById(id: Int): GetChatByIdResult {
        val response: BaseResponse<GetChatByIdResponse> = client.runGet(
            path = "api/v1/chats/$id"
        )

        return GetChatByIdMapper.responseToGetChatById(response)
    }
}
