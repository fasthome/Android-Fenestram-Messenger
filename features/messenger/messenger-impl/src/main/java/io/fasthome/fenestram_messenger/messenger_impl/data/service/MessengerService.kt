package io.fasthome.fenestram_messenger.messenger_impl.data.service

import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.GetChatByIdMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.GetChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.PostChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.SendMessageMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.uikit.paging.ListWithTotal
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData

class MessengerService(
    clientFactory: NetworkClientFactory,
    private val getChatsMapper: GetChatsMapper,
    private val getChatByIdMapper: GetChatByIdMapper
) {
    private val client = clientFactory.create()

    suspend fun sendMessage(id: Long, text: String, type: String, localId: String): SendMessageResult {
        val response: SendMessageResponse = client.runPost(
            path = "api/v1/chats/message/$id",
            body = SendMessageRequest(text, type)
        )

        return SendMessageMapper.responseToSendMessageResult(response, localId)
    }

    suspend fun getChats(limit: Int, page: Int): ListWithTotal<Chat> {
        val response: GetChatsResponse = client.runGet(
            path = "api/v1/chats",
            params = mapOf("limit" to limit, "page" to page)
        )

        return ListWithTotal(
            list = getChatsMapper.responseToGetChatsResult(response),
            totalCount = response.total
        )
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

    suspend fun getMessagesByChat(id: Long, limit: Int, page: Int): MessagesPage {
        val response: GetMessagesResponse = client.runGet(
            path = "api/v1/chats/$id/messages",
            params = mapOf("limit" to limit, "page" to page)
        )
        if(response.data == null) throw Exception()
        return response.let {
            with(getChatsMapper){
                val list = responseToGetMessagesByChat(it.data!!)
                MessagesPage(
                    page = page,
                    total = it.total,
                    messages = list
                )
            }
        }
    }

}
