package io.fasthome.fenestram_messenger.messenger_impl.data.repo_impl

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.data.MessengerSocket
import io.fasthome.fenestram_messenger.messenger_impl.data.service.MessengerService
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.Message
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SendMessageResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SocketMessage
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatByIdResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.GetChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.PostChatsResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.domain.repo.MessengerRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult
import io.fasthome.network.tokens.AccessToken
import io.socket.client.Socket
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

class MessengerImpl(
    private val messengerService: MessengerService,
    private val socket: MessengerSocket
) : MessengerRepo {
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


    override fun getClientSocket(token: AccessToken, callback: Message.() -> Unit) {
        socket.setClientSocket(token) {
            callback(this)
        }
    }

    override fun closeSocket() {
        socket.closeClientSocket()
    }
}
