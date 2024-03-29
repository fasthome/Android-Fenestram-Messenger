package io.fasthome.fenestram_messenger.messenger_impl.data

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.MESSAGE_TYPE_SYSTEM
import io.fasthome.network.model.BaseResponse
import io.fasthome.network.tokens.AccessToken
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    private var socketScope = CoroutineScope(Dispatchers.IO + Job())

    fun setClientSocket(
        chatId: String?,
        token: AccessToken,
        selfUserId: Long?,
        messageCallback: suspend MessageResponseWithChatId.() -> Unit,
        messageActionCallback: MessageActionResponse.() -> Unit,
        messageStatusCallback: MessageStatusResponse.() -> Unit,
        messageDeletedCallback: SocketDeleteMessage.() -> Unit,
        chatChangesCallback: SocketChatChanges.ChatChangesResponse.() -> Unit,
        chatDeletedCallback: SocketDeletedChat.SocketDeletedResponse.() -> Unit,
        chatCreatedCallback: () -> Unit,
        unreadCountCallback: BadgeResponse.() -> Unit,
        reactionsCallback: ReactionsResponse.() -> Unit
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
                socketScope.launch {
                    if (message.message?.type == MESSAGE_TYPE_SYSTEM) {
                        messageCallback(messageToMessageResponse(message.message))
                        return@launch
                    }
                    if (chatId == null || chatId == message.message?.chatId) {
                        if (selfUserId != message.message?.initiatorId) {
                            messageCallback(messageToMessageResponse(message.message))
                        }
                    }
                }
            }

            socket?.on("receiveForwardMessage") {
                Log.d(this.javaClass.simpleName, "receiveForwardMessage: " + it[0].toString())
                val message = json.decodeFromString<SocketForwardMessage>(it[0].toString())

                socketScope.launch {
                    messageCallback(messageToMessageResponse(message.message))
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
                        if(messageStatus.type == MESSAGE_TYPE_SYSTEM && messageStatus.text.contains("создал(а) чат")) {
                            chatCreatedCallback()
                        } else {
                            messageStatusCallback(messageStatus)
                        }
                    }
                }
            }

            socket?.on("receiveChatChanges") {
                Log.d(this.javaClass.simpleName, "receiveChatChanges: " + it[0].toString())
                val chatChanges = json.decodeFromString<SocketChatChanges>(it[0].toString())
                if (chatId?.toLong() == chatChanges.data.chatId && chatChanges.data.updatedValues != null) {
                    chatChangesCallback(chatChanges.data)
                }
            }

            socket?.on("deleteChat") {
                Log.d(this.javaClass.simpleName, "deleteChat: " + it[0].toString())
                val deletedChat = json.decodeFromString<SocketDeletedChat>(it[0].toString())
                chatDeletedCallback(deletedChat.data)
            }

            socket?.on("receiveTotalPendingMessages") {
                Log.d(this.javaClass.simpleName, "receiveTotalPendingMessages: " + it[0].toString())
                val unreadCount = json.decodeFromString<BadgeResponse>(it[0].toString())
                unreadCountCallback(unreadCount)
            }

            socket?.on("receiveReactions") {
                Log.d(this.javaClass.simpleName, "receiveReactions: " + it[0].toString())
                val reactions = json.decodeFromString<BaseResponse<ReactionsResponse>>(it[0].toString())
                if (chatId != null && reactions.data != null && chatId.toLong() == reactions.data!!.chatId) {
                    reactionsCallback(reactions.data!!)
                }
            }
        } catch (e: Exception) {
        }
    }

    fun getChatChanges(
        chatId: Long,
        chatChangesCallback: SocketChatChanges.ChatChangesResponse.() -> Unit
    ) {
        socket?.on("receiveChatChanges") {
            Log.d(this.javaClass.simpleName, "receiveChatChanges: " + it[0].toString())
            val chatChanges = json.decodeFromString<SocketChatChanges>(it[0].toString())
            if (chatId == chatChanges.data.chatId && chatChanges.data.updatedValues != null) {
                chatChangesCallback(chatChanges.data)
            }
        }
    }

    fun emitMessageAction(chatId: String, action: String) {
        val messageActionRequest = JSONObject("{chat_id:$chatId,action:$action}")
        Log.d("emitMessageAction", messageActionRequest.toString())
        socket?.emit("messageAction", messageActionRequest)
    }

    fun emitMessageRead(chatId: Long, messages: List<Long>) {
        val messageReadRequest = JSONObject("{chat_id:$chatId,messages:$messages}")
        Log.d("emitMessageRead", messageReadRequest.toString())
        socket?.emit("messageRead", messageReadRequest)
    }

    fun emitChatListeners(subChatId: Long?, unsubChatId: Long?) {
        val messageReadRequest = JSONObject("{sub:$subChatId,unsub:$unsubChatId}")
        Log.d("emitChatListeners", messageReadRequest.toString())
        socket?.emit("chatListeners", messageReadRequest)
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
        status = "",
        replyMessage = message?.replyMessage,
        usersHaveRead = message?.usersHaveRead ?: listOf(),
        forwardedMessages = message?.forwardedMessages,
        content = message?.content ?: emptyList()
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