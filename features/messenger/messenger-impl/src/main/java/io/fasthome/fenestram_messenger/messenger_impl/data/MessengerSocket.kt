package io.fasthome.fenestram_messenger.messenger_impl.data

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SocketMessage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.MESSAGE_TYPE_SYSTEM
import io.fasthome.network.tokens.AccessToken
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Collections.singletonList
import java.util.Collections.singletonMap

class MessengerSocket(private val baseUrl: String) {

    private var socket: Socket? = null

    fun setClientSocket(
        chatId: String?,
        token: AccessToken,
        selfUserId: Long?,
        callback: MessageResponseWithChatId.() -> Unit,
    ) {
        try {
            val opts = IO.Options()
            opts.extraHeaders =
                singletonMap("Authorization", singletonList("Bearer ${token.s}"))
            opts.reconnection = true
            socket = IO.socket(baseUrl.dropLast(1), opts)
            socket?.connect()
            socket?.on("receiveMessage") {
                Log.d(this.javaClass.simpleName, "receiveMessage: " + it[0].toString())
                val message = Json.decodeFromString<SocketMessage>(it[0].toString())
                if(message.message?.type == MESSAGE_TYPE_SYSTEM) {
                    callback(messageToMessageResponse(message.message))
                    return@on
                }
                if (chatId == null || chatId == message.message?.chatId) {
                    if (selfUserId != message.message?.initiatorId) {
                        callback(messageToMessageResponse(message.message))
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    fun messageToMessageResponse(message: MessageResponseWithChatId?) = MessageResponseWithChatId(
        id = message?.id ?: 1,
        initiatorId = message?.initiatorId ?: 1L,
        text = message?.text ?: "",
        type = message?.type ?: "",
        date = message?.date ?: "",
        initiator = message?.initiator,
        chatId = message?.chatId,
        replyMessageId = message?.replyMessageId,
        authorId = message?.authorId,
        access = message?.access ?: emptyList(),
        accessChats = message?.accessChats ?: emptyList(),
        isEdited = message?.isEdited ?: false,
        status = ""
    )

    fun closeClientSocket() {
        socket?.close()
        socket = null
    }
}