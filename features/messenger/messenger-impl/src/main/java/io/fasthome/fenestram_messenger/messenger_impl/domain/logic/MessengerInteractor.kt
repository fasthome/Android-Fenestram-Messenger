package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import android.util.Log
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageActionResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.FilesRepo
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.onSuccess
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

    suspend fun postChats(name: String, users: List<Long>, isGroup: Boolean) =
        messageRepo.postChats(name, users, isGroup)

    suspend fun patchChatAvatar(id: Long, avatar: String) =
        messageRepo.patchChatAvatar(id, avatar)

    suspend fun getChatById(id: Long) = messageRepo.getChatById(id).onSuccess { }

    fun closeSocket() {
        messageRepo.closeSocket()
    }

    suspend fun getMessagesFromChat(id: Long, selfUserId: Long): Flow<Message> {
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
            })

        return messagesFlow
    }

    fun emitMessageAction(chatId: String, action: String) {
        messageRepo.emitMessageAction(chatId, action)
    }

    suspend fun getNewMessages(): Flow<Message> {
        messageRepo.getClientSocket(
            chatId = null,
            token = tokensRepo.getAccessToken(),
            callback = object : MessengerRepo.SocketMessageCallback {
                override fun onNewMessage(message: MessageResponseWithChatId) {
                    _newMessagesChannel.trySend(chatsMapper.toMessage(message))
                }

                override fun onNewMessageAction(messageAction: MessageActionResponse) {
                    Log.d("messageAction", messageAction.user?.name.toString())
                }
            },
            selfUserId = null
        )
        return newMessagesFlow
    }

    suspend fun deleteChat(id: Long) = messageRepo.deleteChat(id)

    suspend fun deleteMessage(messageId: Long, chatId: Long) =
        messageRepo.deleteMessage(messageId, chatId)


    fun getMessengerPageItems(query: String): TotalPagingSource<Int, Chat> =
        messageRepo.getPageChats(query)

    private var page = 0

    suspend fun getChatPageItems(isResumed: Boolean, id: Long): CallResult<MessagesPage> {
        //todo isResumed оставить, возможно вернется баг с загрузкой из onResume
        page++
        return messageRepo.getMessagesFromChat(id, page)
    }

    suspend fun uploadProfileImage(photoBytes: ByteArray) =
        messageRepo.uploadImage(photoBytes, UUID.randomUUID().toString())

    suspend fun editMessage(chatId: Long, messageId: Long, newText: String) =
        messageRepo.editMessage(chatId = chatId, messageId = messageId, newText = newText)

    suspend fun getFile(itemId: String): CallResult<FileData?> =
        filesRepo.getFile(itemId)


    suspend fun saveFile(itemId: String, tempFile: File) {
        filesRepo.saveFile(itemId = itemId, tempFile.readBytes(), tempFile.name)
    }

    suspend fun getUserId() = userStorage.getUserId()
}
