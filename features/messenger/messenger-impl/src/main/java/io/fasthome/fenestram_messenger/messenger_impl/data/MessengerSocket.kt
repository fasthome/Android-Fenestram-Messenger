package io.fasthome.fenestram_messenger.messenger_impl.data

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageActionResponse
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.MessageResponseWithChatId
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SocketMessage
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.SocketMessageAction
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.MESSAGE_TYPE_SYSTEM
import io.fasthome.network.tokens.AccessToken
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.util.Collections.singletonList
import java.util.Collections.singletonMap

class MessengerSocket(private val baseUrl: String) {

    private var socket: Socket? = null

    fun setClientSocket(
        chatId: String?,
        token: AccessToken,
        selfUserId: Long?,
        messageCallback: MessageResponseWithChatId.() -> Unit,
        messageActionCallback: MessageActionResponse.() -> Unit
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
                if (message.message?.type == MESSAGE_TYPE_SYSTEM) {
                    messageCallback(messageToMessageResponse(message.message))
                    return@on
                }
                if (chatId == null || chatId == message.message?.chatId) {
                    if (selfUserId != message.message?.initiatorId) {
                        messageCallback(messageToMessageResponse(message.message))
                    }
                }
            }

            socket?.on("receiveMessageAction") {
                Log.d(this.javaClass.simpleName, "receiveMessageAction: " + it[0].toString())
                val messageAction = Json.decodeFromString<SocketMessageAction>(it[0].toString())
//                if (selfUserId != messageAction.message.user?.id) {
                messageActionCallback(messageActionToMessageActionResponse(messageAction.message))
//                }
            }

        } catch (e: Exception) {
        }
    }

    fun emitMessageAction(chatId: String, action: String) {
        val messageActionRequest = JSONObject("{chat_id:$chatId,action:$action}")
        socket?.emit("messageAction", messageActionRequest)
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

    fun messageActionToMessageActionResponse(message: MessageActionResponse?) =
        MessageActionResponse(
            user = message?.user,
            action = message?.action
        )

    fun closeClientSocket() {
        socket?.close()
        socket = null
    }
}