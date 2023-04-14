package io.fasthome.fenestram_messenger.push_impl.data.service.mapper

import android.content.Intent
import android.util.Log
import io.fasthome.fenestram_messenger.push_impl.domain.entity.PushClickData

class RemoteMessagesMapper {

    fun getPushClickData(intent: Intent): PushClickData = getPushClickData(intent.extrasMap())

    private fun getPushClickData(data: Map<String, Any?>?): PushClickData {
        Log.d("Push-Notification", "data=$data")

        if (data == null) return PushClickData.Unknown

        return if (data["is_group"]?.toString().toBoolean()) {
            PushClickData.Chat(
                chatId = data["chat_id"]?.toString(),
                userAvatar = data["chat_avatar"]?.toString(),
                chatName = data["chat_name"]?.toString(),
                isGroup = data["is_group"]?.toString().toBoolean()
            )
        } else if (data["is_group"] == null) {
            PushClickData.Unknown
        } else {
            val userName = when {
                !data["user_contact_name"]?.toString().isNullOrEmpty() -> data["user_contact_name"]?.toString()
                !data["user_name"]?.toString().isNullOrEmpty() -> data["user_name"]?.toString()
                !data["user_nickname"]?.toString().isNullOrEmpty() -> data["user_nickname"]?.toString()
                else -> ""
            }
            PushClickData.Chat(
                chatId = data["chat_id"]?.toString(),
                userAvatar = data["user_avatar"]?.toString(),
                chatName = userName,
                isGroup = data["is_group"]?.toString().toBoolean()
            )
        }
    }

    private fun Intent.extrasMap(): Map<String, Any?>? {
        val extras = extras ?: return null
        return extras.keySet().map { it to extras.get(it) }.toMap()
    }
}