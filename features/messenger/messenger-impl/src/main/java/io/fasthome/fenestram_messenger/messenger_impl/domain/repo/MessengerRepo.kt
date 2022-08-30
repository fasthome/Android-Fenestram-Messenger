package io.fasthome.fenestram_messenger.messenger_impl.domain.repo

import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.network.tokens.AccessToken

interface MessengerRepo {
    suspend fun sendMessage(id: Long, text: String, type: String): CallResult<SendMessageResult>
    suspend fun getChats(selfUserId : Long, limit: Int, page: Int): CallResult<GetChatsResult>
    suspend fun postChats(name: String, users: List<Long>, isGroup: Boolean): CallResult<PostChatsResult>
    suspend fun getChatById(id: Long): CallResult<GetChatByIdResult>
    fun closeSocket()
    fun getClientSocket(token: AccessToken, callback: MessageResponse.() -> Unit)
}