package io.fasthome.fenestram_messenger.messenger_impl.data.repo_impl

import io.fasthome.fenestram_messenger.messenger_impl.data.service.MessengerService
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult

class MessengerImpl(private val messengerService: MessengerService) : MessengerRepo {
    override suspend fun sendMessage(
        id: Int,
        text: String,
        type: String
    ): CallResult<SendMessageResult> = callForResult {
        messengerService.sendMessage(id, text, type)
    }

    override suspend fun getChats(limit: Int, page: Int): CallResult<GetChatsResult> =
        callForResult {
            messengerService.getChats(limit, page)
        }

    override suspend fun postChats(name: String, users: List<Int>): CallResult<PostChatsResult> =
        callForResult {
            messengerService.postChats(name, users)
        }

    override suspend fun getChatById(id: Int): CallResult<GetChatByIdResult> = callForResult {
        messengerService.getChatById(id)
    }
}