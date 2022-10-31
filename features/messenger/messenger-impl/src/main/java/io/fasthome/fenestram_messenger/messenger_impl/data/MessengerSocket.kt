package io.fasthome.fenestram_messenger.messenger_impl.data

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
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

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun setClientSocket(
        chatId: String?,
        token: AccessToken,
        selfUserId: Long?,
        messageCallback: MessageResponseWithChatId.() -> Unit,
        messageActionCallback: MessageActionResponse.() -> Unit,
        messageStatusCallback: MessageStatusResponse.() -> Unit,
        pendingMessagesCallback: PendingMessagesResponse.() -> Unit,
        messageDeletedCallback: SocketDeleteMessage.() -> Unit
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
                val message = json.decodeFromString<SocketMessage>(it[0].toString())
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
                val messageAction = json.decodeFromString<SocketMessageAction>(it[0].toString())
                messageActionCallback(messageActionToMessageActionResponse(messageAction.message))
            }

            socket?.on("receiveDeleteMessage") {
                Log.d(this.javaClass.simpleName, "receiveDeleteMessage: " + it[0].toString())
                val messageAction = json.decodeFromString<SocketDeleteMessage>(it[0].toString())
                messageDeletedCallback(messageAction)
            }

            socket?.on("receiveMessageStatus") {
                Log.d(this.javaClass.simpleName, "receiveMessageStatus: " + it[0].toString())
                val messageStatuses = json.decodeFromString<SocketMessageStatus>(it[0].toString())
                messageStatuses.messages?.forEach { messageStatus ->
                    if (messageStatus != null) {
                        messageStatusCallback(messageStatus)
                    }
                }
            }

            socket?.on("chatPendingMessages") {
                Log.d(this.javaClass.simpleName, "chatPendingMessages: " + it[0].toString())
                val pendingMessages =
                    json.decodeFromString<PendingMessagesResponse>(it[0].toString())
                pendingMessagesCallback(pendingMessages)
            }

        } catch (e: Exception) {
        }
    }

    fun emitMessageAction(chatId: String, action: String) {
        val messageActionRequest = JSONObject("{chat_id:$chatId,action:$action}")
        Log.d("emitMessageAction", messageActionRequest.toString())
        socket?.emit("messageAction", messageActionRequest)
    }

    fun emitMessageRead(chatId: Long, messages: List<Long>) {
        val messageReadRequest = JSONObject("{chat_id:$chatId,messages:$messages}")
        Log.d("emitMessageRead", messages.toString())
        socket?.emit("messageRead", messageReadRequest)
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

    fun messageActionToMessageActionResponse(messageAction: MessageActionResponse?) =
        MessageActionResponse(
            user = messageAction?.user,
            action = messageAction?.action,
            chatId = messageAction?.chatId
        )

    fun closeClientSocket() {
        socket?.close()
        socket = null
    }
}