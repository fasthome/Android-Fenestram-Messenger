package io.fasthome.fenestram_messenger.messenger_impl.data.repo_impl

import io.fasthome.fenestram_messenger.messenger_impl.data.MessengerSocket
import io.fasthome.fenestram_messenger.messenger_impl.data.service.MessengerService
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.*
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult
import io.fasthome.network.tokens.AccessToken

class MessengerRepoImpl(
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

    override suspend fun getChats(selfUserId : Long, limit: Int, page: Int): CallResult<GetChatsResult> =
        callForResult {
            messengerService.getChats(selfUserId, limit, page)
        }

    override suspend fun postChats(name: String, users: List<Long>, isGroup: Boolean): CallResult<PostChatsResult> =
        callForResult {
            messengerService.postChats(name, users, isGroup)
        }

    override suspend fun getChatById(id: Long): CallResult<GetChatByIdResult> = callForResult {
        messengerService.getChatById(id)
    }

    override suspend fun getMessagesFromChat(id: Long): CallResult<List<Message>> = callForResult {
        messengerService.getMessagesByChat(id)
    }

    override fun getClientSocket(token: AccessToken, callback: MessengerRepo.SocketMessageCallback) {
        socket.setClientSocket(token) {
            callback.onNewMessage(this)
        }
    }

    override fun closeSocket() {
        socket.closeClientSocket()
    }
}
