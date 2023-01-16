package io.fasthome.fenestram_messenger.messenger_impl.domain.repo

import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
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

    suspend fun replyMessage(
        chatId: Long,
        messageId: Long,
        text: String,
        messageType: String
    ): CallResult<Message?>

    suspend fun forwardMessage(chatId: Long, messageId: Long): CallResult<Message?>

    fun getPageChats(query: String, fromSocket: Boolean): TotalPagingSource<Int, Chat>

    suspend fun postChats(
        name: String,
        users: List<Long>,
        isGroup: Boolean
    ): CallResult<PostChatsResult>

    suspend fun patchChatAvatar(id: Long, avatar: String): CallResult<Unit>

    suspend fun getChatById(id: Long): CallResult<GetChatByIdResult>
    suspend fun getMessagesFromChat(id: Long, limit: Int, page: Int): CallResult<MessagesPage>
    suspend fun deleteChat(id: Long): CallResult<Unit>
    suspend fun deleteMessage(messageId: Long, chatId: Long, fromAll: Boolean): CallResult<Unit>

    fun closeSocket()

    suspend fun getDocument(
        storagePath: String,
        progressListener: ProgressListener
    ): CallResult<LoadedDocumentData>

    fun getClientSocket(
        chatId: String?,
        token: AccessToken,
        callback: SocketMessageCallback,
        selfUserId: Long?
    )

    fun getChatChanges(
        chatId: Long,
        onNewChatChanges: (SocketChatChanges.ChatChangesResponse) -> Unit
    )

    fun emitMessageAction(chatId: String, action: String)
    fun emitMessageRead(chatId: Long, messages: List<Long>)
    fun emitChatListeners(subChatId: Long?, unsubChatId: Long?)

    suspend fun uploadImage(photoBytes: ByteArray, guid: String): CallResult<UploadImageResult>
    suspend fun editMessage(chatId: Long, messageId: Long, newText: String): CallResult<Unit>
    suspend fun uploadDocument(
        chatId: Long,
        documentBytes: ByteArray,
        guid: String
    ): CallResult<SendMessageResponse>

    interface SocketMessageCallback {
        fun onNewMessage(message: MessageResponseWithChatId)
        fun onNewMessageAction(messageAction: MessageActionResponse)
        fun onNewMessageStatus(messageStatusResponse: MessageStatusResponse)
        fun onMessageDeleted(socketDeleteMessage: SocketDeleteMessage)
        fun onNewChatChanges(chatChangesResponse: SocketChatChanges.ChatChangesResponse)
    }
}