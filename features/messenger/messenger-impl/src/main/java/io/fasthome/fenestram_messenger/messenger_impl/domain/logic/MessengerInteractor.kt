package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import io.fasthome.fenestram_messenger.messenger_impl.data.service.mapper.ChatsMapper.toMessage
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.tokens.TokensRepo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class MessengerInteractor(
    private val messageRepo: MessengerRepo,
    private val tokensRepo: TokensRepo
) {

    suspend fun sendMessage(id: Long, text: String, type: String) =
        messageRepo.sendMessage(id, text, type).onSuccess { }

    suspend fun getChats(selfUserId : Long, limit: Int, page: Int) = messageRepo.getChats(selfUserId, limit, page)

    suspend fun postChats(name: String, users: List<Long>) =
        messageRepo.postChats(name, users).onSuccess { }

    suspend fun getChatById(id: Long) = messageRepo.getChatById(id).onSuccess { }

    fun closeSocket() {
        messageRepo.closeSocket()
    }

    fun getMessagesFromChat(id: Long): Flow<Message> = callbackFlow {
        messageRepo.getChatById(id).onSuccess { result ->
            if (result is GetChatByIdResult.Success)
                result.messages?.reversed()?.forEach {
                    if (it != null) {
                        trySend(it.toMessage())
                    }
                }
        }
        messageRepo.getClientSocket(tokensRepo.getAccessToken()) {
            trySend(this.toMessage())
        }
        awaitClose()
    }
}
