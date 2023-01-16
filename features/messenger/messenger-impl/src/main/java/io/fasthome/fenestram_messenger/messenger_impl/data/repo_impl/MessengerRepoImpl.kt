package io.fasthome.fenestram_messenger.messenger_impl.data.repo_impl

import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.data.MessengerSocket
import io.fasthome.fenestram_messenger.messenger_impl.data.service.MessengerService
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.LoadedDocumentData
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SendMessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SocketChatChanges
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.uikit.paging.ListWithTotal
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper.Companion.PAGE_SIZE
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.uikit.paging.totalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult
import io.fasthome.network.client.ProgressListener
import io.fasthome.network.tokens.AccessToken

class MessengerRepoImpl(
    private val messengerService: MessengerService,
    private val socket: MessengerSocket,
    private val storageUrlConverter: StorageUrlConverter
) : MessengerRepo {

    private var currrentList: ListWithTotal<Chat> = ListWithTotal(listOf(), 0)

    override suspend fun sendMessage(
        id: Long,
        text: String,
        type: String,
        localId: String,
        authorId: Long
    ): CallResult<SendMessageResult> = callForResult {
        messengerService.sendMessage(id, text, type, localId, authorId)
    }

    override suspend fun forwardMessage(chatId: Long, messageId: Long): CallResult<Message?> = callForResult {
        messengerService.forwardMessage(chatId, messageId)
    }


    override suspend fun replyMessage(
        chatId: Long,
        messageId: Long,
        text: String,
        messageType: String,
    ): CallResult<Message?> = callForResult {
        messengerService.replyMessage(
            messageId = messageId,
            chatId = chatId,
            text = text,
            messageType = messageType
        )
    }

    override fun getPageChats(query: String, fromSocket: Boolean): TotalPagingSource<Int, Chat> {
        return if (!fromSocket)
            totalPagingSource(
                maxPageSize = PAGE_SIZE,
                loadPageService = { pageNumber, pageSize ->
                    currrentList = messengerService.getChats(
                        query = query,
                        page = pageNumber,
                        limit = pageSize
                    )
                    currrentList
                }
            )
        else
            totalPagingSource(PAGE_SIZE) { _, _ -> currrentList }
    }

    override suspend fun postChats(
        name: String,
        users: List<Long>,
        isGroup: Boolean
    ): CallResult<PostChatsResult> =
        callForResult {
            messengerService.postChats(name, users, isGroup)
        }

    override suspend fun patchChatAvatar(id: Long, avatar: String): CallResult<Unit> =
        callForResult {
            messengerService.patchChatAvatar(id, avatar)
        }

    override suspend fun getChatById(id: Long): CallResult<GetChatByIdResult> = callForResult {
        messengerService.getChatById(id)
    }

    override suspend fun getMessagesFromChat(
        id: Long,
        limit: Int,
        page: Int
    ): CallResult<MessagesPage> =
        callForResult {
            messengerService.getMessagesByChat(id = id, limit = limit, page = page)
        }

    override suspend fun deleteChat(id: Long) = callForResult {
        messengerService.deleteChat(id)
    }

    override suspend fun deleteMessage(messageId: Long, chatId: Long, fromAll: Boolean) = callForResult {
        messengerService.deleteMessage(messageId, chatId, fromAll)
    }

    override fun getClientSocket(
        chatId: String?,
        token: AccessToken,
        callback: MessengerRepo.SocketMessageCallback,
        selfUserId: Long?
    ) {
        socket.setClientSocket(
            chatId = chatId,
            token = token,
            selfUserId = selfUserId,
            messageCallback = { callback.onNewMessage(this) },
            messageActionCallback = { callback.onNewMessageAction(this) },
            messageStatusCallback = { callback.onNewMessageStatus(this) },
            messageDeletedCallback = { callback.onMessageDeleted(this) },
            chatChangesCallback = { callback.onNewChatChanges(this) }
        )
    }

    override fun getChatChanges(
        chatId: Long,
        onNewChatChanges: (SocketChatChanges.ChatChangesResponse) -> Unit
    ) {
        socket.getChatChanges(chatId) {
            onNewChatChanges(this)
        }
    }

    override fun emitMessageAction(chatId: String, action: String) {
        socket.emitMessageAction(chatId, action)
    }

    override fun emitMessageRead(chatId: Long, messages: List<Long>) {
        socket.emitMessageRead(chatId, messages)
    }

    override fun emitChatListeners(subChatId: Long?, unsubChatId: Long?) {
        socket.emitChatListeners(subChatId, unsubChatId)
    }

    override suspend fun uploadImage(
        photoBytes: ByteArray,
        guid: String
    ): CallResult<UploadImageResult> = callForResult {
        messengerService.uploadImage(photoBytes, guid)
    }

    override suspend fun editMessage(
        chatId: Long,
        messageId: Long,
        newText: String
    ): CallResult<Unit> = callForResult {
        messengerService.editMessage(chatId = chatId, messageId = messageId, newText = newText)
    }

    override suspend fun uploadDocument(
        chatId: Long,
        documentBytes: ByteArray,
        guid: String
    ): CallResult<SendMessageResponse> = callForResult {
        messengerService.uploadDocument(documentBytes, guid, chatId)
    }

    override suspend fun getDocument(
        storagePath: String,
        progressListener: ProgressListener
    ): CallResult<LoadedDocumentData> = callForResult {
        messengerService.getDocument(storageUrlConverter.convert(storagePath), progressListener)
    }

    override fun closeSocket() {
        socket.closeClientSocket()
    }
}
