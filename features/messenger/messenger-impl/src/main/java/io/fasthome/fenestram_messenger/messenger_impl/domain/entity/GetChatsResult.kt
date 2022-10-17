package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.User
import java.time.ZonedDateTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val id: Long?,
    val name: String,
    val users: List<Long>,
    val messages: List<Message>,
    val time: ZonedDateTime?,
    val avatar: String?,
    val isGroup: Boolean
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
    val isDate : Boolean,
    val isEdited: Boolean,
    val replyMessage: Message?
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
            replyMessage = null
        )
    }
}