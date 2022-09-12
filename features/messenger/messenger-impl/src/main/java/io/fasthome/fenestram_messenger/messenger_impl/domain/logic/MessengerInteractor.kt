package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.tokens.TokensRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MessengerInteractor(
    private val messageRepo: MessengerRepo,
    private val tokensRepo: TokensRepo,
    private val chatsMapper: ChatsMapper
) {
    private val _messagesChannel =
        Channel<List<Message>>(onBufferOverflow = BufferOverflow.DROP_OLDEST, onUndeliveredElement = { list ->
            list.forEach {
                Log.d("MessengerInteractor", "onUndeliveredElement " + it)
            }
        })
    private val _newMessagesChannel =
        Channel<Message>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val messagesFlow: Flow<List<Message>> = _messagesChannel.receiveAsFlow()
    private val newMessagesFlow: Flow<Message> = _newMessagesChannel.receiveAsFlow()

    suspend fun sendMessage(id: Long, text: String, type: String) =
        messageRepo.sendMessage(id, text, type).onSuccess { }

    suspend fun getChats(selfUserId: Long, limit: Int, page: Int) =
        messageRepo.getChats(selfUserId, limit, page)

    suspend fun postChats(name: String, users: List<Long>, isGroup: Boolean) =
        messageRepo.postChats(name, users, isGroup)

    suspend fun getChatById(id: Long) = messageRepo.getChatById(id).onSuccess { }

    fun closeSocket() {
        messageRepo.closeSocket()
    }

    suspend fun getMessagesFromChat(id: Long): Flow<List<Message>> {
        messageRepo.getMessagesFromChat(id).onSuccess { result ->
            result.reversed().let {
                _messagesChannel.trySend(it)
            }
        }
        messageRepo.getClientSocket(
            chatId = id.toString(),
            token = tokensRepo.getAccessToken(),
            callback = object : MessengerRepo.SocketMessageCallback {
                override fun onNewMessage(message: MessageResponseWithChatId) {
                    _messagesChannel.trySend(listOf(chatsMapper.toMessage(message)))
                }
            })

        return messagesFlow
    }

    suspend fun getMessages(id: Long): CallResult<List<Message>> = messageRepo.getMessagesFromChat(id)

    suspend fun getNewMessages(): Flow<Message> {
        messageRepo.getClientSocket(
            chatId = null,
            token = tokensRepo.getAccessToken(),
            callback = object : MessengerRepo.SocketMessageCallback {
                override fun onNewMessage(message: MessageResponseWithChatId) {
                    this
                    _messagesChannel.trySend(listOf(chatsMapper.toMessage(message)))
                }
            })
        return newMessagesFlow
    }
}
