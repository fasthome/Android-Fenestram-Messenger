package io.fasthome.fenestram_messenger.messenger_impl.domain.repo

import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageActionResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.LoadedDocumentData
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.network.client.ProgressListener
import io.fasthome.network.tokens.AccessToken

interface MessengerRepo {

    suspend fun sendMessage(
        id: Long,
        text: String,
        type: String,
        localId: String,
        authorId: Long
    ): CallResult<SendMessageResult>

    suspend fun replyMessage(chatId: Long, messageId: Long, text: String, messageType: String): CallResult<Unit>

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
    suspend fun deleteMessage(messageId: Long, chatId: Long): CallResult<Unit>

    fun closeSocket()

    suspend fun getDocument(storagePath : String, progressListener: ProgressListener) : CallResult<LoadedDocumentData>

    fun getClientSocket(
        chatId: String?,
        token: AccessToken,
        callback: SocketMessageCallback,
        selfUserId: Long?
    )

    fun emitMessageAction(chatId: String, action: String)

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): CallResult<UploadImageResult>
    suspend fun editMessage(chatId: Long, messageId: Long, newText: String): CallResult<Unit>
    suspend fun uploadDocument(documentBytes: ByteArray, guid: String): CallResult<UploadDocumentResult>

    interface SocketMessageCallback {
        fun onNewMessage(message: MessageResponseWithChatId)
        fun onNewMessageAction(messageAction: MessageActionResponse)
    }
}