package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import android.util.Log
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.messenger_api.entity.ChatChanges
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.FilesRepo
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.uikit.paging.PagingDataViewModelHelper.Companion.PAGE_SIZE
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.client.ProgressListener
import io.fasthome.network.tokens.TokensRepo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File
import java.util.*

class MessengerInteractor(
    private val messageRepo: MessengerRepo,
    private val tokensRepo: TokensRepo,
    private val chatsMapper: ChatsMapper,
    private val filesRepo: FilesRepo,
    private val userStorage: UserStorage
) {
    private val _messagesChannel =
        Channel<Message>(
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
            onUndeliveredElement = { message ->
                Log.d("MessengerInteractor", "onUndeliveredElement " + message)
            })
    private val _newMessagesChannel =
        Channel<Message>(onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _messageActionsChannel =
        Channel<MessageAction>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val messagesFlow: Flow<Message> = _messagesChannel.receiveAsFlow()
    private val newMessagesFlow: Flow<Message> = _newMessagesChannel.receiveAsFlow()
    val messageActionsFlow: Flow<MessageAction> = _messageActionsChannel.receiveAsFlow()

    suspend fun sendMessage(id: Long, text: String, type: String, localId: String, authorId: Long) =
        messageRepo.sendMessage(id, text, type, localId, authorId)

    suspend fun forwardMessage(chatId: Long, messageId: Long) = messageRepo.forwardMessage(chatId, messageId)

    suspend fun postChats(name: String, users: List<Long>, isGroup: Boolean) =
        messageRepo.postChats(name, users, isGroup)

    suspend fun patchChatAvatar(id: Long, avatar: String) =
        messageRepo.patchChatAvatar(id, avatar)

    suspend fun getChatById(id: Long) = messageRepo.getChatById(id).onSuccess { }

    fun closeSocket() {
        messageRepo.closeSocket()
    }

    suspend fun replyMessage(chatId: Long, messageId: Long, text: String, messageType: String) =
        messageRepo.replyMessage(
            chatId = chatId,
            messageId = messageId,
            text = text,
            messageType = messageType
        )

    suspend fun getMessagesFromChat(
        id: Long,
        selfUserId: Long,
        onNewMessageStatusCallback: (MessageStatus) -> Unit,
        onMessageDeletedCallback: (List<Long>) -> Unit,
        onNewChatChangesCallback: (ChatChanges) -> Unit
    ): Flow<Message> {
        messageRepo.getClientSocket(
            chatId = id.toString(),
            selfUserId = selfUserId,
            token = tokensRepo.getAccessToken(),
            callback = object : MessengerRepo.SocketMessageCallback {
                override fun onNewMessage(message: MessageResponseWithChatId) {
                    _messagesChannel.trySend(chatsMapper.toMessage(message))
                }

                override fun onNewMessageAction(messageAction: MessageActionResponse) {
                    _messageActionsChannel.trySend(chatsMapper.toMessageAction(messageAction))
                }

                override fun onNewMessageStatus(messageStatusResponse: MessageStatusResponse) {
                    if (selfUserId == messageStatusResponse.initiatorId && id == messageStatusResponse.chatId) {
                        onNewMessageStatusCallback(chatsMapper.toMessageStatus(messageStatusResponse))
                    }
                }

                override fun onMessageDeleted(socketDeleteMessage: SocketDeleteMessage) {
                    onMessageDeletedCallback(socketDeleteMessage.message.map { it.id })
                }

                override fun onNewChatChanges(chatChangesResponse: SocketChatChanges.ChatChangesResponse) {
                    onNewChatChangesCallback(chatsMapper.toChatChanges(chatChangesResponse))
                }

            })

        return messagesFlow
    }

    fun getChatChanges(
        chatId: Long,
        onNewChatChanges: (ChatChanges) -> Unit
    ) {
        messageRepo.getChatChanges(chatId) {
            onNewChatChanges(chatsMapper.toChatChanges(it))
        }
    }

    fun emitMessageAction(chatId: String, action: String) {
        messageRepo.emitMessageAction(chatId, action)
    }

    fun emitMessageRead(chatId: Long, messages: List<Long>) {
        if (messages.isNotEmpty()) {
            messageRepo.emitMessageRead(chatId, messages)
        }
    }

    fun emitChatListeners(subChatId: Long?, unsubChatId: Long?) {
        messageRepo.emitChatListeners(subChatId, unsubChatId)
    }

    suspend fun getNewMessages(
        onNewMessageStatusCallback: (MessageStatus) -> Unit,
        onNewChatChangesCallback: (ChatChanges) -> Unit
    ): Flow<Message> {
        messageRepo.getClientSocket(
            chatId = null,
            token = tokensRepo.getAccessToken(),
            callback = object : MessengerRepo.SocketMessageCallback {
                override fun onNewMessage(message: MessageResponseWithChatId) {
                    _newMessagesChannel.trySend(chatsMapper.toMessage(message))
                }

                override fun onNewMessageAction(messageAction: MessageActionResponse) {
                    _messageActionsChannel.trySend(chatsMapper.toMessageAction(messageAction))
                }

                override fun onNewMessageStatus(messageStatusResponse: MessageStatusResponse) {
                    onNewMessageStatusCallback(chatsMapper.toMessageStatus(messageStatusResponse))
                }

                override fun onMessageDeleted(socketDeleteMessage: SocketDeleteMessage) {
                }

                override fun onNewChatChanges(chatChangesResponse: SocketChatChanges.ChatChangesResponse) {
                    onNewChatChangesCallback(chatsMapper.toChatChanges(chatChangesResponse))
                }
            },
            selfUserId = null
        )
        return newMessagesFlow
    }

    suspend fun deleteChat(id: Long) = messageRepo.deleteChat(id)

    suspend fun deleteMessage(messageId: Long, chatId: Long, fromAll: Boolean) =
        messageRepo.deleteMessage(messageId, chatId, fromAll)


    fun getMessengerPageItems(query: String, fromSocket: Boolean): TotalPagingSource<Int, Chat> =
        messageRepo.getPageChats(query, fromSocket)

    private var page = 0

    suspend fun getChatPageItems(
        isResumed: Boolean,
        newMessagesCount: Int,
        id: Long
    ): CallResult<MessagesPage> {
        if (isResumed) page = 0
        page++
        return if (newMessagesCount != 0) {
            page += newMessagesCount / (page * PAGE_SIZE)
            messageRepo.getMessagesFromChat(id, page * PAGE_SIZE, 1)
        } else {
            messageRepo.getMessagesFromChat(id, PAGE_SIZE, page)
        }
    }

    suspend fun uploadProfileImage(photoBytes: ByteArray) =
        messageRepo.uploadImage(photoBytes, UUID.randomUUID().toString())

    suspend fun uploadDocument(chatId: Long, documentBytes: ByteArray, name: String) =
        messageRepo.uploadDocument(chatId = chatId, documentBytes, name)

    suspend fun getDocument(storagePath: String, progressListener: ProgressListener) =
        messageRepo.getDocument(storagePath, progressListener)

    suspend fun editMessage(chatId: Long, messageId: Long, newText: String) =
        messageRepo.editMessage(chatId = chatId, messageId = messageId, newText = newText)

    suspend fun getFile(itemId: String): CallResult<FileData?> =
        filesRepo.getFile(itemId)


    suspend fun saveFile(itemId: String, tempFile: File) {
        filesRepo.saveFile(itemId = itemId, tempFile.readBytes(), tempFile.name)
    }

    suspend fun clearFiles() = filesRepo.clearFiles()

    suspend fun getUserId() = userStorage.getUserId()

    fun clearLastInstance() {
        page = 0
    }
}
