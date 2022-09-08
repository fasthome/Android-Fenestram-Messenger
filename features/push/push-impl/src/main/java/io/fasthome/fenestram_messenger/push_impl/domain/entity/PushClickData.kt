package io.fasthome.fenestram_messenger.push_impl.domain.entity

sealed interface PushClickData {

    class Chat(
        val chatId : String?,
        val chatName : String?,
        val userAvatar : String?,
        val isGroup : Boolean?
    ) : PushClickData

    object Unknown : PushClickData
}