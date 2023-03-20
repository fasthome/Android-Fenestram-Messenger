package io.fasthome.fenestram_messenger.messenger_impl.data.service

import io.fasthome.fenestram_messenger.messenger_api.entity.Badge
import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.*
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.uikit.paging.ListWithTotal
import io.fasthome.fenestram_messenger.util.ProgressListener
import io.fasthome.fenestram_messenger.util.model.MetaInfo
import io.fasthome.network.client.NetworkClientFactory
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.util.requireData
import io.ktor.client.statement.*
import java.util.*

class MessengerService(
    clientFactory: NetworkClientFactory,
    private val getChatsMapper: GetChatsMapper,
    private val getChatByIdMapper: GetChatByIdMapper,
    private val contentMapper: ContentMapper,
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

    suspend fun postChats(name: String, users: List<Long?>, isGroup: Boolean): PostChatsResult {
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

    suspend fun uploadAvatar(chatId: Long, photoBytes: ByteArray): PatchChatAvatarRequest {
        val response = client.runSubmitFromWithPatchImages<BaseResponse<ChatAvatarResponse>>(
            path = "chats/$chatId/avatar",
            binaryDatas = listOf(photoBytes),
            filename = listOf(UUID.randomUUID().toString())
        )
        return PatchChatAvatarRequest(response.data?.avatarUrl)
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

    suspend fun uploadImages(
        imageBytes: List<ByteArray>,
        chatId: Long,
        filename: List<String>
    ): SendMessageResponse {
        val response = client
            .runSubmitFormWithImages<BaseResponse<SendMessageResponse>>(
                path = "chats/$chatId/file_message",
                binaryDatas = imageBytes,
                filename = filename
            )
            .requireData()
        return response
    }

    suspend fun uploadDocuments(
        documentBytes: List<ByteArray>,
        guid: List<String>,
        chatId: Long
    ): List<MetaInfo> {
        return client
            .runSubmitFormWithDocument<BaseResponse<SendMessageResponse>>(
                path = "chats/$chatId/file_message",
                binaryDatas = documentBytes,
                filename = guid,
            )
            .requireData()
            .let {
                it.message?.content?.map(contentMapper::mapContentResponseToMetaInfo) ?: listOf()
            }
    }

    suspend fun getDocument(url: String, progressListener: ProgressListener): LoadedDocumentData {
        val httpResponse = client.runGet<HttpResponse>(
            path = url,
            useBaseUrl = false
        ) { progress, loadedBytesSize, fullBytesSize, isReady ->
            progressListener(progress, loadedBytesSize, fullBytesSize, isReady)
        }
        val byteArray = httpResponse.readBytes()
        return LoadedDocumentData(byteArray = byteArray)
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
                path = "chats/message/$chatId",
                body = DeleteMessageRequest(true, listOf(messageId.toString()))
            )
    }

    suspend fun forwardMessage(chatId: Long, messageId: Long): Message? {
        val response: BaseResponse<ForwardMessageReponse> =
            client.runPost(
                path = "chats/forward/message/$chatId",
                body = ForwardMessageRequest(listOf(messageId.toString()))
            )
        return if (response.data?.message != null) getChatsMapper.responseToMessage(response.data!!.message!!) else null
    }

    suspend fun replyMessage(
        messageId: Long,
        chatId: Long,
        text: String,
        messageType: String
    ): Message? {
        val response: BaseResponse<ReplyMessageResponse> =
            client.runPost(
                path = "chats/reply/message/$chatId/$messageId",
                body = ReplyMessageRequest(text, messageType)
            )
        return if (response.data?.message != null) getChatsMapper.responseToMessage(response.data!!.message!!) else null
    }

    suspend fun editMessage(messageId: Long, chatId: Long, newText: String) {
        val response: BaseResponse<EditMessageResponse> =
            client.runPatch(
                path = "chats/message/$chatId/$messageId",
                body = EditMessageRequest(newText)
            )
    }

    suspend fun fetchUnreadCount(): Badge {
        val response: BaseResponse<BadgeResponse> =
            client.runGet(
                path = "chats/total_pending_messages",
            )

        return response.requireData().let {
            Badge(count = it.totalPending)
        }
    }

    suspend fun postReaction(chatId: Long, messageId: Long, reaction: String) {
        val response: BaseResponse<ReactionsResponse> = client.runPost(
            path = "chats/$chatId/message/$messageId/reaction",
            body = PostReactionRequest(reaction = reaction)
        )
    }

}
