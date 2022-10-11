package io.fasthome.fenestram_messenger.messenger_impl.data.service

import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.*
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

    suspend fun sendMessage(
        id: Long,
        text: String,
        type: String,
        localId: String,
        authorId: Long
    ): SendMessageResult {
        val response: BaseResponse<SendMessageResponse> = client.runPost(
            path = "chats/message/$id",
            body = SendMessageRequest(text, type, replyMessageId = null, authorId = authorId)
        )
        
        return SendMessageMapper.responseToSendMessageResult(response.requireData(), localId)
    }

    suspend fun getChats(query: String, limit: Int, page: Int): ListWithTotal<Chat> {
        val response: GetChatsResponse = client.runGet(
            path = "chats",
            params = mapOf("like" to query, "limit" to limit, "page" to page)
        )

        return ListWithTotal(
            list = getChatsMapper.responseToGetChatsResult(response),
            totalCount = response.total
        )
    }

    suspend fun postChats(name: String, users: List<Long>, isGroup: Boolean): PostChatsResult {
        return client
            .runPost<PostChatsRequest, BaseResponse<PostChatsResponse>>(
                path = "chats",
                body = PostChatsRequest(name, users, isGroup)
            )
            .requireData()
            .let {
                PostChatsMapper.responseToPostChatsResult(it)
            }
    }

    suspend fun patchChatAvatar(id: Long, avatar: String) {
        client.runPatch<PatchChatAvatarRequest, BaseResponse<PatchChatAvatarRequest>>(
            path = "chats/$id/avatar",
            body = PatchChatAvatarRequest(avatar)
        )
    }

    suspend fun getChatById(id: Long): GetChatByIdResult {
        val response: BaseResponse<GetChatByIdResponse> = client.runGet(
            path = "chats/$id"
        )

        return getChatByIdMapper.responseToGetChatById(response)
    }

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): UploadImageResult {
        val response = client
            .runSubmitFormWithFile<BaseResponse<UploadImageResponse>>(
                path = "files/upload",
                binaryData = photoBytes,
                filename = "$guid.jpg",
            )
            .requireData()
        return UploadImageResult(imagePath = response.pathToFile)
    }

    suspend fun getMessagesByChat(id: Long, limit: Int, page: Int): MessagesPage {
        val response: GetMessagesResponse = client.runGet(
            path = "chats/$id/messages",
            params = mapOf("limit" to limit, "page" to page)
        )
        if (response.data == null) throw Exception()
        return response.let {
            with(getChatsMapper) {
                val list = responseToGetMessagesByChat(it.data!!)
                MessagesPage(
                    page = page,
                    total = it.total,
                    messages = list
                )
            }
        }
    }

    suspend fun deleteChat(id: Long) {
        val response: BaseResponse<DeleteChatResponse> =
            client.runDelete<DeleteChatRequest?, BaseResponse<DeleteChatResponse>>(
                path = "chats/$id",
                body = null
            )
    }

    suspend fun deleteMessage(messageId: Long, chatId: Long) {
        val response: BaseResponse<DeleteMessageResponse> =
            client.runDelete<DeleteMessageRequest?, BaseResponse<DeleteMessageResponse>>(
                path = "chats/message/$chatId/$messageId",
                body = DeleteMessageRequest(true)
            )
    }

    suspend fun editMessage(messageId: Long, chatId: Long,newText: String) {
        val response: BaseResponse<MessageFullResponse> =
            client.runPatch(
                path = "chats/message/$chatId/$messageId",
                body = EditMessageRequest(newText)
            )
    }

}
