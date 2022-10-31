package io.fasthome.fenestram_messenger.messenger_impl.data.repo_impl

import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.data.MessengerSocket
import io.fasthome.fenestram_messenger.messenger_impl.data.service.MessengerService
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.LoadedDocumentData
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
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

    override suspend fun sendMessage(
        id: Long,
        text: String,
        type: String,
        localId: String,
        authorId: Long
    ): CallResult<SendMessageResult> = callForResult {
        messengerService.sendMessage(id, text, type, localId, authorId)
    }

    override suspend fun replyMessage(
        chatId: Long,
        messageId: Long,
        text: String,
        messageType: String,
    ) = callForResult {
        messengerService.replyMessage(
            messageId = messageId,
            chatId = chatId,
            text = text,
            messageType = messageType)
    }

    override fun getPageChats(query: String): TotalPagingSource<Int, Chat> = totalPagingSource(
        maxPageSize = PAGE_SIZE,
        loadPageService = { pageNumber, pageSize ->
            messengerService.getChats(
                query = query,
                page = pageNumber,
                limit = pageSize
            )
        }
    )

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

    override suspend fun getMessagesFromChat(id: Long, page: Int): CallResult<MessagesPage> =
        callForResult {
            messengerService.getMessagesByChat(id = id, limit = PAGE_SIZE, page = page)
        }

    override suspend fun deleteChat(id: Long) = callForResult {
        messengerService.deleteChat(id)
    }

    override suspend fun deleteMessage(messageId: Long, chatId: Long) = callForResult {
        messengerService.deleteMessage(messageId, chatId)
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
            messageActionCallback = { callback.onNewMessageAction(this) }
        )
    }

    override fun emitMessageAction(chatId: String, action: String) {
        socket.emitMessageAction(chatId, action)
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



    override suspend fun uploadDocument(documentBytes: ByteArray, guid: String): CallResult<UploadDocumentResult> = callForResult {
        messengerService.uploadDocument(documentBytes, guid)
    }

    override suspend fun getDocument(storagePath : String, progressListener: ProgressListener): CallResult<LoadedDocumentData> = callForResult {
        messengerService.getDocument(storageUrlConverter.convert(storagePath), progressListener)
    }

    override fun closeSocket() {
        socket.closeClientSocket()
    }
}
