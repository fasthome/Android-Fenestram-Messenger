package io.fasthome.fenestram_messenger.messenger_impl.domain.repo

import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.fenestram_messenger.util.CallResult

interface MessengerRepo {
    suspend fun sendMessage(id: Int, text: String, type: String): CallResult<SendMessageResult>
    suspend fun getChats(limit: Int, page: Int): CallResult<GetChatsResult>
    suspend fun postChats(name: String, users: List<Int>): CallResult<PostChatsResult>
    suspend fun getChatById(id: Int): CallResult<GetChatByIdResult>
}