package io.fasthome.fenestram_messenger.messenger_impl.data.service

import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.GetChatByIdMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.GetChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.PostChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.SendMessageMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class MessengerService(
    clientFactory: NetworkClientFactory,
    private val getChatsMapper: GetChatsMapper,
    private val getChatByIdMapper: GetChatByIdMapper
) {
    private val client = clientFactory.create()

    suspend fun sendMessage(id: Long, text: String, type: String): SendMessageResult {
        val response: SendMessageResponse = client.runPost(
            path = "api/v1/chats/message/$id",
            body = SendMessageRequest(text, type)
        )

        return SendMessageMapper.responseToSendMessageResult(response)
    }

    suspend fun getChats(selfUserId: Long, limit: Int, page: Int): GetChatsResult {
        val response: GetChatsResponse = client.runGet(
            path = "api/v1/chats",
            params = mapOf("limit" to limit, "page" to page)
        )

        return getChatsMapper.responseToGetChatsResult(response, selfUserId)
    }

    suspend fun postChats(name: String, users: List<Long>, isGroup: Boolean): PostChatsResult {
        return client
            .runPost<PostChatsRequest, BaseResponse<PostChatsResponse>>(
                path = "api/v1/chats",
                body = PostChatsRequest(name, users, isGroup)
            )
            .requireData()
            .let {
                PostChatsMapper.responseToPostChatsResult(it)
            }
    }

    suspend fun getChatById(id: Long): GetChatByIdResult {
        val response: BaseResponse<GetChatByIdResponse> = client.runGet(
            path = "api/v1/chats/$id"
        )

        return getChatByIdMapper.responseToGetChatById(response)
    }

    suspend fun getMessagesByChat(id: Long): List<Message> {
        val response: BaseResponse<List<MessageResponse>> = client.runGet(
            path = "api/v1/chats/$id/messages",
            params = mapOf("limit" to 1000, "page" to 1)
        )

        return response.requireData().let(getChatsMapper::responseToGetMessagesByChat)
    }

}
