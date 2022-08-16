package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.ZonedDateTime

sealed class GetChatsResult {
    class Success(
        val chats : List<Chat>
    ) : GetChatsResult()
}

@Parcelize
data class Chat(
    val id : Long?,
    val name : String,
    val users: List<User>,
    val messages : List<Message>,
    val time : ZonedDateTime?
) : Parcelable

@Parcelize
data class User(
    val id : Long?
): Parcelable

@Parcelize
data class Message(
    val id: Long,
    val text: String,
    val userSenderId : Long,
    val messageType: String,
    val date: ZonedDateTime?
): Parcelable