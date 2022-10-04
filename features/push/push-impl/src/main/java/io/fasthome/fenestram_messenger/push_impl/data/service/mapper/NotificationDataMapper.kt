package io.fasthome.fenestram_messenger.push_impl.data.service.mapper

import io.fasthome.fenestram_messenger.push_impl.domain.entity.NotificationData

class NotificationDataMapper {

    fun getNotificationData(data: Map<String, String>): NotificationData {
        val userName = when {
            !data["user_contact_name"].isNullOrEmpty() -> data["user_contact_name"]
            !data["user_nickname"].isNullOrEmpty() -> data["user_nickname"]
            !data["user_name"].isNullOrEmpty() -> data["user_name"]
            else -> "Test Push"
        } ?: ""

        return NotificationData(
            chatId = data["chat_id"]?.toInt(),
            chatName = data["chat_name"] ?: "",
            chatAvatar = data["chat_avatar"] ?: "",
            userId = data["user_id"] ?: "",
            userName = userName,
            userAvatar = data["user_avatar"] ?: "",
            text = data["text"] ?: "",
            isGroup = data["is_group"].toBoolean()
        )
    }
}