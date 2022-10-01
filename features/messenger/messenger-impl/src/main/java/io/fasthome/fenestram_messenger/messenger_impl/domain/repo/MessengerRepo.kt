package io.fasthome.fenestram_messenger.messenger_impl.domain.repo

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.network.tokens.AccessToken

interface MessengerRepo {

    suspend fun sendMessage(
        id: Long,
        text: String,
        type: String,
        localId: String
    ): CallResult<SendMessageResult>

    fun getPageChats(query: String): TotalPagingSource<Int, Chat>

    suspend fun postChats(
        name: String,
        users: List<Long>,
        isGroup: Boolean
    ): CallResult<PostChatsResult>

    suspend fun patchChatAvatar(id: Long, avatar: String): CallResult<Unit>

    suspend fun getChatById(id: Long): CallResult<GetChatByIdResult>
    suspend fun getMessagesFromChat(id: Long, page: Int): CallResult<MessagesPage>
    suspend fun deleteChat(id: Long): CallResult<Unit>

    fun closeSocket()

    fun getClientSocket(
        chatId: String?,
        token: AccessToken,
        callback: SocketMessageCallback,
        selfUserId: Long?
    )

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): CallResult<UploadImageResult>
    suspend fun uploadDocument(documentBytes: ByteArray, guid: String): CallResult<UploadDocumentResult>

    interface SocketMessageCallback {
        fun onNewMessage(message: MessageResponseWithChatId)
    }
}