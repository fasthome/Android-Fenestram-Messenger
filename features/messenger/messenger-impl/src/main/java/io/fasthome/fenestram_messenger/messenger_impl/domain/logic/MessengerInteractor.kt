package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.tokens.TokensRepo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class MessengerInteractor(
    private val messageRepo: MessengerRepo,
    private val tokensRepo: TokensRepo,
    private val chatsMapper: ChatsMapper
) {
    private val _messagesChannel =
        Channel<List<Message>>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val messagesFlow: Flow<List<Message>> = _messagesChannel.receiveAsFlow()

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
        messageRepo.getClientSocket(tokensRepo.getAccessToken()) {
            _messagesChannel.trySend(listOf(chatsMapper.toMessage(this)))
        }

        return messagesFlow
    }
}
