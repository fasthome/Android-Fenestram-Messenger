package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.Initiator
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.ZonedDateTime

sealed class GetChatsResult {
    class Success(
        val chats: List<Chat>
    ) : GetChatsResult()
}

@Parcelize
data class Chat(
    val id: Long?,
    val name: String,
    val users: List<Long>,
    val messages: List<Message>,
    val time: ZonedDateTime?,
    val avatar: String?,
    val isGroup : Boolean
) : Parcelable

@Parcelize
data class Message(
    val id: Long,
    val text: String,
    val userSenderId: Long,
    val messageType: String,
    val date: ZonedDateTime?,
    val initiator : User?,
    val chatId : String? = null
) : Parcelable