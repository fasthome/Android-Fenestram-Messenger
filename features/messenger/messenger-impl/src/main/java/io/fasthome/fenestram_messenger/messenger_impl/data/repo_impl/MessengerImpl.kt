package io.fasthome.fenestram_messenger.messenger_impl.data.repo_impl

import io.fasthome.fenestram_messenger.messenger_impl.data.MessengerSocket
import io.fasthome.fenestram_messenger.messenger_impl.data.service.MessengerService
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult
import io.fasthome.network.tokens.AccessToken

class MessengerImpl(
    private val messengerService: MessengerService,
    private val socket: MessengerSocket
) : MessengerRepo {
    override suspend fun sendMessage(
        id: Long,
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

    override suspend fun getChatById(id: Long): CallResult<GetChatByIdResult> = callForResult {
        messengerService.getChatById(id)
    }


    override fun getClientSocket(token: AccessToken, callback: MessageResponse.() -> Unit) {
        socket.setClientSocket(token) {
            callback(this)
        }
    }

    override fun closeSocket() {
        socket.closeClientSocket()
    }
}
