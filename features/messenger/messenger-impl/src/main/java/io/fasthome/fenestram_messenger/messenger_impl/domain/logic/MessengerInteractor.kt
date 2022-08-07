package io.fasthome.fenestram_messenger.messenger_impl.domain.logic

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.onSuccess
import io.fasthome.network.tokens.TokensRepo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class MessengerInteractor(
    private val messageRepo: MessengerRepo,
    private val tokensRepo: TokensRepo
) {

    suspend fun sendMessage(id: Int, text: String, type: String) =
        messageRepo.sendMessage(id, text, type).onSuccess { }

    suspend fun getChats(limit: Int, page: Int) = messageRepo.getChats(limit, page).onSuccess { }

    suspend fun postChats(name: String, users: List<Int>) =
        messageRepo.postChats(name, users).onSuccess { }

    suspend fun getChatById(id: Int) = messageRepo.getChatById(id).onSuccess { }

    fun closeSocket() {
        messageRepo.closeSocket()
    }

    fun getMessagesFromChat(id: Int): Flow<Message?> = callbackFlow {
        messageRepo.getChatById(id).onSuccess { result ->
            if (result is GetChatByIdResult.Success)
                result.messages?.reversed()?.forEach { trySend(it) }
        }
        messageRepo.getClientSocket(tokensRepo.getAccessToken()) {
            trySend(this)
        }
        awaitClose()
    }
}
