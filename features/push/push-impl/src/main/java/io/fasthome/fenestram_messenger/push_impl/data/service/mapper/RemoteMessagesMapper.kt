package io.fasthome.fenestram_messenger.push_impl.data.service.mapper

import android.content.Intent
import android.util.Log
import io.fasthome.fenestram_messenger.push_impl.domain.entity.PushClickData

class RemoteMessagesMapper {

    fun getPushClickData(intent: Intent): PushClickData = getPushClickData(intent.extrasMap())

    private fun getPushClickData(data: Map<String, Any?>?): PushClickData {
        Log.d("Push-Notification", "data=$data")

        if (data == null) return PushClickData.Unknown
        val chatName = if (data["is_group"]?.toString().toBoolean()) {
            data["chat_name"]?.toString()
        } else {
            data["user_name"]?.toString()
        }
        return PushClickData.Chat(
            chatId = data["chat_id"]?.toString(),
            userAvatar = data["user_avatar"]?.toString(),
            chatName = chatName,
            isGroup = data["is_group"]?.toString().toBoolean()
        )
    }

    private fun Intent.extrasMap(): Map<String, Any?>? {
        val extras = extras ?: return null
        return extras.keySet().map { it to extras.get(it) }.toMap()
    }
}