package io.fasthome.fenestram_messenger.messenger_impl.domain.repo

import io.fasthome.fenestram_messenger.messenger_api.entity.Badge
import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.ProgressListener
import io.fasthome.fenestram_messenger.util.model.MetaInfo
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

    fun getPageChats(query: String): TotalPagingSource<Int, Chat>

    suspend fun updateBdChatsFromService(query: String): CallResult<Unit>

    suspend fun updateBdChatsStatus(chatId: Long, status: SentStatus): CallResult<Unit>

    fun getCachedPages(): TotalPagingSource<Int, Chat>

    suspend fun postChats(
        name: String,
        users: List<Long?>,
        isGroup: Boolean
    ): CallResult<PostChatsResult>

    suspend fun postReaction(chatId: Long, messageId: Long, reaction: String): CallResult<Unit>

    suspend fun patchChatAvatar(id: Long, avatar: String): CallResult<Unit>

    suspend fun getChatById(id: Long): CallResult<GetChatByIdResult>

    suspend fun addNewMessageToDb(message: Message): CallResult<Unit>
    suspend fun getMessagesFromChat(id: Long, limit: Int, page: Int): CallResult<MessagesPage>
    suspend fun deleteChat(id: Long): CallResult<Unit>
    suspend fun deleteChatFromDb(chatId: Long): CallResult<Unit>
    suspend fun deleteMessage(messageId: Long, chatId: Long): CallResult<Unit>

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

    suspend fun uploadAvatar(chatId: Long, photoBytes: ByteArray): CallResult<PatchChatAvatarRequest>
    suspend fun editMessage(chatId: Long, messageId: Long, newText: String): CallResult<Unit>
    suspend fun uploadDocuments(
        chatId: Long,
        documentBytes: List<ByteArray>,
        guid: List<String>
    ): CallResult<List<MetaInfo>>

    suspend fun uploadImages(
        chatId: Long,
        imagesBytes: List<ByteArray>,
        filename: List<String>,
    ): CallResult<SendMessageResponse>

    suspend fun clearChats()

    suspend fun fetchUnreadCount(): CallResult<Badge>

    interface SocketMessageCallback {
        fun onNewMessage(message: MessageResponseWithChatId)
        fun onNewMessageAction(messageAction: MessageActionResponse)
        fun onNewMessageStatus(messageStatusResponse: MessageStatusResponse)
        fun onMessageDeleted(socketDeleteMessage: SocketDeleteMessage)
        fun onNewChatChanges(chatChangesResponse: SocketChatChanges.ChatChangesResponse)
        fun onDeletedChatCallback(chatDeletedChat: SocketDeletedChat.SocketDeletedResponse)
        fun onUnreadMessage(badgeResponse: BadgeResponse)
        fun onNewReactionCallback(reactionsResponse: ReactionsResponse)
        fun onNewChatCreated()
    }
}