package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessagesPage
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.uikit.paging.TotalPagingSource
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.tokens.TokensRepo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.*

class MessengerInteractor(
    private val messageRepo: MessengerRepo,
    private val tokensRepo: TokensRepo,
    private val chatsMapper: ChatsMapper,
) {
    private val _messagesChannel =
        Channel<Message>(
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
            onUndeliveredElement = { message ->
                Log.d("MessengerInteractor", "onUndeliveredElement " + message)
            })
    private val _newMessagesChannel =
        Channel<Message>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val messagesFlow: Flow<Message> = _messagesChannel.receiveAsFlow()
    private val newMessagesFlow: Flow<Message> = _newMessagesChannel.receiveAsFlow()

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
            })

        return messagesFlow
    }

    suspend fun getNewMessages(): Flow<Message> {
        messageRepo.getClientSocket(
            chatId = null,
            token = tokensRepo.getAccessToken(),
            callback = object : MessengerRepo.SocketMessageCallback {
                override fun onNewMessage(message: MessageResponseWithChatId) {
                    _newMessagesChannel.trySend(chatsMapper.toMessage(message))
                }
            },
            selfUserId = null
        )
        return newMessagesFlow
    }

    suspend fun deleteChat(id: Long) = messageRepo.deleteChat(id)

    suspend fun deleteMessage(messageId: Long, chatId: Long) = messageRepo.deleteMessage(messageId, chatId)


    fun getMessengerPageItems(query: String): TotalPagingSource<Int, Chat> =
        messageRepo.getPageChats(query)

    private var page = 0

    suspend fun getChatPageItems(isResumed: Boolean, id: Long): CallResult<MessagesPage> {
        if(isResumed) {
            page = 0
        }
        page++
        return messageRepo.getMessagesFromChat(id, page)
    }

    suspend fun uploadProfileImage(photoBytes: ByteArray) =
        messageRepo.uploadImage(photoBytes, UUID.randomUUID().toString())
}
