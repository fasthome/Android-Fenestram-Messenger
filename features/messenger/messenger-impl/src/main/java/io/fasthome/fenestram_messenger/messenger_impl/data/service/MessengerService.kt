package io.fasthome.fenestram_messenger.messenger_impl.data.service

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
        localId: String
    ): SendMessageResult {
        val response: SendMessageResponse = client.runPost(
            path = "api/v1/chats/message/$id",
            body = SendMessageRequest(text, type)
        )

        return SendMessageMapper.responseToSendMessageResult(response, localId)
    }

    suspend fun getChats(query: String, limit: Int, page: Int): ListWithTotal<Chat> {
        val response: GetChatsResponse = client.runGet(
            path = "api/v1/chats",
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
                path = "api/v1/chats",
                body = PostChatsRequest(name, users, isGroup)
            )
            .requireData()
            .let {
                PostChatsMapper.responseToPostChatsResult(it)
            }
    }

    suspend fun patchChatAvatar(id: Long, avatar: String) {
        client.runPatch<PatchChatAvatarRequest, BaseResponse<PatchChatAvatarRequest>>(
            path = "api/v1/chats/$id/avatar",
            body = PatchChatAvatarRequest(avatar)
        )
    }

    suspend fun getChatById(id: Long): GetChatByIdResult {
        val response: BaseResponse<GetChatByIdResponse> = client.runGet(
            path = "api/v1/chats/$id"
        )

        return getChatByIdMapper.responseToGetChatById(response)
    }

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): UploadImageResult {
        val response = client
            .runSubmitFormWithFile<BaseResponse<UploadImageResponse>>(
                path = "api/v1/files/upload",
                binaryData = photoBytes,
                filename = "$guid.jpg",
            )
            .requireData()
        return UploadImageResult(imagePath = response.pathToFile)
    }

    suspend fun uploadDocument(documentBytes: ByteArray, guid: String): UploadDocumentResult {
        val response = client
            .runSubmitFormWithFile<BaseResponse<UploadDocumentResponse>>(
                path = "api/v1/files/upload",
                binaryData = documentBytes,
                filename = guid,
            )
            .requireData()
        return UploadDocumentResult(documentPath = response.pathToFile)
    }

    suspend fun getMessagesByChat(id: Long, limit: Int, page: Int): MessagesPage {
        val response: GetMessagesResponse = client.runGet(
            path = "api/v1/chats/$id/messages",
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
                path = "api/v1/chats/$id",
                body = null
            )
    }

}
