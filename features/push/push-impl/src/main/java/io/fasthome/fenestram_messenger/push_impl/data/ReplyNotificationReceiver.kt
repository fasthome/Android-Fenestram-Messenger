package io.fasthome.fenestram_messenger.push_impl.data

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.push_impl.data.FirebasePushService.Companion.KEY_TEXT_REPLY
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZonedDateTime

class ReplyNotificationReceiver : BroadcastReceiver(), KoinComponent {

    private val scope = CoroutineScope(DispatchersProvider.Default)
    private val messengerFeature by inject<MessengerFeature>()
    private val userStorage by inject<UserStorage>()

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val inputtedText =
            RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)?.trim()
                ?: return
        val notificationId = intent.getStringExtra("chat_id")?.toInt() ?: 0

        scope.launch {
            val selfUserId = userStorage.getUserId() ?: return@launch
            if (messengerFeature.sendMessage(
                    id = notificationId.toLong(),
                    text = inputtedText.toString(),
                    type = "text",
                    localId = "",
                    authorId = selfUserId
                ) is CallResult.Error
            ) {
                notificationManager.cancel(notificationId)
                return@launch
            }

            val notification = notificationManager.activeNotifications.firstOrNull {
                it.id == notificationId
            }?.notification ?: return@launch

            val recoveredNotificationBuilder =
                Notification.Builder.recoverBuilder(context, notification).also {
                    val messageStyle = it.style as Notification.MessagingStyle
                    messageStyle.addMessage(
                        inputtedText,
                        ZonedDateTime.now().toInstant().toEpochMilli(),
                        messageStyle.user
                    )
                    it.style = messageStyle
                }

            notificationManager.notify(notificationId, recoveredNotificationBuilder.build())
        }
    }

}