package io.fasthome.fenestram_messenger.messenger_impl.data.service

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

class MessengerService(clientFactory: NetworkClientFactory, private val getChatsMapper: GetChatsMapper) {
    private val client = clientFactory.create()

    suspend fun sendMessage(id: Long, text: String, type: String): SendMessageResult {
        val response: SendMessageResponse = client.runPost(
            path = "api/v1/chats/message/$id",
            body = SendMessageRequest(text, type)
        )

        return SendMessageMapper.responseToSendMessageResult(response)
    }

    suspend fun getChats(selfUserId : Long, limit: Int, page: Int): GetChatsResult {
        val response: GetChatsResponse = client.runGet(
            path = "api/v1/chats",
            params = mapOf("limit" to limit, "page" to page)
        )

        return getChatsMapper.responseToGetChatsResult(response, selfUserId)
    }

    suspend fun postChats(name: String, users: List<Long>, isGroup: Boolean): PostChatsResult {
        val response: BaseResponse<PostChatsResponse> = client.runPost(
            path = "api/v1/chats",
            body = PostChatsRequest(name, users, isGroup)
        )

        return PostChatsMapper.responseToPostChatsResult(response)
    }

    suspend fun getChatById(id: Long): GetChatByIdResult {
        val response: BaseResponse<GetChatByIdResponse> = client.runGet(
            path = "api/v1/chats/$id"
        )

        return GetChatByIdMapper.responseToGetChatById(response)
    }
}
