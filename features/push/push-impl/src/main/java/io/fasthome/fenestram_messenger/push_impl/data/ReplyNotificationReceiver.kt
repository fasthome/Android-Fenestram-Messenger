package io.fasthome.fenestram_messenger.push_impl.data

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.push_impl.data.FirebasePushService.Companion.KEY_TEXT_REPLY
import io.fasthome.fenestram_messenger.push_impl.domain.repo.PushRepo
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZonedDateTime

class ReplyNotificationReceiver : BroadcastReceiver(), KoinComponent {

    private val scope = CoroutineScope(DispatchersProvider.Default)
    private val pushRepo by inject<PushRepo>()

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val inputtedText =
            RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)?.trim()
                ?: return
        val notificationId = intent.getStringExtra("chat_id")?.toInt() ?: 0

        scope.launch {
            if (pushRepo.sendMessage(notificationId.toLong(), inputtedText.toString(), "text")
                        is CallResult.Error
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