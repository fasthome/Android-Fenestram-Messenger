package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper.toMessage
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.User
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.tokens.TokensRepo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class MessengerInteractor(
    private val messageRepo: MessengerRepo,
    private val tokensRepo: TokensRepo
) {
    private val _messagesChannel = Channel<List<Message>>(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val messagesFlow: Flow<List<Message>> = _messagesChannel.receiveAsFlow()

    suspend fun sendMessage(id: Long, text: String, type: String) =
        messageRepo.sendMessage(id, text, type).onSuccess { }

    suspend fun getChats(selfUserId: Long, limit: Int, page: Int) = messageRepo.getChats(selfUserId, limit, page)

    suspend fun postChats(name: String, users: List<User>) =
        messageRepo.postChats(name, users.mapNotNull { it.id })

    suspend fun getChatById(id: Long) = messageRepo.getChatById(id).onSuccess { }

    fun closeSocket() {
        messageRepo.closeSocket()
    }

    suspend fun getMessagesFromChat(id: Long): Flow<List<Message>> {
        messageRepo.getChatById(id).onSuccess { result ->
            if (result is GetChatByIdResult.Success) {
                result.messages?.reversed()?.let {
                    _messagesChannel.trySend(it.mapNotNull { response->
                        response?.toMessage()
                    })
                }
            }
        }
        messageRepo.getClientSocket(tokensRepo.getAccessToken()) {
            _messagesChannel.trySend(listOf(this.toMessage()))
        }

        return messagesFlow
    }
}
