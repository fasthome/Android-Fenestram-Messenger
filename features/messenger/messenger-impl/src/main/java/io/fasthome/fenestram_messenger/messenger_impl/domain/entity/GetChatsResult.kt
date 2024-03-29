package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.util.model.MetaInfo
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class Chat(
    val id: Long?,
    val name: String,
    val users: List<Long?>,
    val messages: List<Message>,
    val time: ZonedDateTime?,
    val avatar: String?,
    val isGroup: Boolean,
    val pendingMessages: Long
) : Parcelable

@Parcelize
data class Message(
    val id: Long,
    val text: String,
    val userSenderId: Long,
    val messageType: String,
    val date: ZonedDateTime?,
    val initiator: User?,
    val chatId: String? = null,
    val isDate: Boolean,
    val isEdited: Boolean,
    val messageStatus: String,
    val replyMessage: Message?,
    val usersHaveRead: List<Long?>?,
    val forwardedMessages: List<Message>?,
    val content: List<MetaInfo>,
    val reactions: Map<String, List<User>>
) : Parcelable {
    companion object {
        fun onlyDate(date: ZonedDateTime) = Message(
            id = 0,
            text = "",
            userSenderId = 0,
            messageType = "",
            date = date,
            initiator = null,
            chatId = null,
            isDate = true,
            isEdited = false,
            messageStatus = "",
            replyMessage = null,
            usersHaveRead = null,
            forwardedMessages = emptyList(),
            content = emptyList(),
            reactions = emptyMap()
        )
    }
}

data class MessageAction(
    val userId: Long,
    val chatId: Long,
    val userName: String,
    val userStatus: UserStatus
)

data class MessageStatus(
    val messageId: Long,
    val messageStatus: String,
    val messageType: String,
    val usersHaveRead: List<Long?>?
)

data class MessageReactions(
    val messageId: Long,
    val reactions: Map<String, List<User>>
)