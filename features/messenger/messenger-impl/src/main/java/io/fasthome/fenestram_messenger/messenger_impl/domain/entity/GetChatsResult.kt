package io.fasthome.fenestram_messenger.messenger_impl.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class GetChatsResult {
    class Success(
        val chats : List<Chat>
    ) : GetChatsResult()
}

@Parcelize
data class Chat(
    val id : Long?,
    val user: User,
    val messages : List<Message>
) : Parcelable

@Parcelize
data class User(
    val id : Long,
    val name: String
): Parcelable

@Parcelize
data class Message(
    val id: Long,
    val text: String,
    val userSenderId : Long,
    val messageType: String,
    val createdAt: String
): Parcelable