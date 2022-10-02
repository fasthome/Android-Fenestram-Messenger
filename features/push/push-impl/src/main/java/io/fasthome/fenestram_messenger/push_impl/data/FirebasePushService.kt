/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.push_impl.data

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.presentation.base.AppFeature
import io.fasthome.fenestram_messenger.presentation.base.util.Channel
import io.fasthome.fenestram_messenger.presentation.base.util.createNotificationChannel
import io.fasthome.fenestram_messenger.push_impl.R
import io.fasthome.fenestram_messenger.push_impl.data.storage.RemoteMessagesStorage
import io.fasthome.fenestram_messenger.push_impl.domain.repo.PushRepo
import io.fasthome.fenestram_messenger.util.PendingIntentCompat
import io.fasthome.fenestram_messenger.util.android.color
import io.fasthome.fenestram_messenger.util.getOrDefault
import io.fasthome.fenestram_messenger.util.kotlin.switchJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.time.ZonedDateTime


class FirebasePushService : FirebaseMessagingService() {

    private val pushRepo by inject<PushRepo>()
    private val remoteMessagesStorage by inject<RemoteMessagesStorage>()
    private val appFeature by inject<AppFeature>()
    private val authFeature by inject<AuthFeature>()
    private val profileImageUrlConverter by inject<ProfileImageUrlConverter>()

    private val scope = CoroutineScope(DispatchersProvider.Default)
    private var initPushesJob by switchJob()

    private val mePerson = Person.Builder().setName("Я").build()

    override fun onNewToken(token: String) {
        initPushesJob = scope.launch {
            if (authFeature.isUserAuthorized().getOrDefault(false)) {
                pushRepo.sendPushToken()
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notificationId =
            message.data["chat_id"]?.toInt() ?: remoteMessagesStorage.nextNotificationId()

        val activityIntent = Intent(this, appFeature.startActivityClazz.java)
        val broadcastIntent = Intent(this, ReplyNotificationReceiver::class.java)
        message.data.forEach { (k, v) ->
            activityIntent.putExtra(k, v)
            broadcastIntent.putExtra(k, v)
        }

        val contentIntent = PendingIntent.getActivity(
            this,
            notificationId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE,
        )
        val replyIntent = PendingIntent.getBroadcast(
            this,
            notificationId,
            broadcastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        createNotificationChannel(Channel.Push)

        val notificationMessage = buildMessage(message.data)
        val messagingStyle = restoreMessagingStyle(notificationId)?.addMessage(notificationMessage)
            ?: if (message.data["is_group"].toBoolean()) {
                NotificationCompat.MessagingStyle(mePerson)
                    .addMessage(notificationMessage)
                    .setConversationTitle(message.data["chat_name"])
                    .setGroupConversation(true)
            } else {
                NotificationCompat.MessagingStyle(mePerson)
                    .addMessage(notificationMessage)
            }

        val notification = NotificationCompat.Builder(this, Channel.Push.id).also {
            if (message.data["is_group"].toBoolean()) {
                it.setLargeIcon(getAvatar(message.data["chat_avatar"]))
            }
            it.setSmallIcon(R.drawable.ic_message)
            it.color = resources.color(R.color.blue)
            it.setStyle(messagingStyle)
            it.setShowWhen(true)
            it.setAutoCancel(true)
            it.setContentIntent(contentIntent)
            it.setGroup(GROUP_HOOLICHAT)
            it.addAction(buildAction(replyIntent))
        }.build()

        val summaryNotification = NotificationCompat.Builder(this, Channel.Push.id)
            .setSmallIcon(R.drawable.ic_message)
            .setStyle(NotificationCompat.InboxStyle())
            .setGroup(GROUP_HOOLICHAT)
            .setGroupSummary(true)
            .build()

        NotificationManagerCompat.from(this).apply {
            notify(notificationId, notification)
            notify(SUMMARY_ID, summaryNotification)
        }
    }

    private fun buildMessage(data: Map<String, String>): NotificationCompat.MessagingStyle.Message {
        val userName = when {
            !data["user_contact_name"].isNullOrEmpty() -> data["user_contact_name"]
            !data["user_nickname"].isNullOrEmpty() -> data["user_nickname"]
            !data["user_name"].isNullOrEmpty() -> data["user_name"]
            else -> "Test Push"
        }

        val iconBitmap = getAvatar(data["user_avatar"])

        return NotificationCompat.MessagingStyle.Message(
            data["text"],
            ZonedDateTime.now().toInstant().toEpochMilli(),
            Person.Builder()
                .setName(userName)
                .setIcon(IconCompat.createWithBitmap(iconBitmap))
                .build()
        )
    }

    private fun buildAction(replyPendingIntent: PendingIntent): NotificationCompat.Action {
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel(resources.getString(R.string.reply_label))
            build()
        }
        return NotificationCompat.Action.Builder(
            R.drawable.ic_baseline_send_24,
            getString(R.string.reply_label),
            replyPendingIntent
        )
            .addRemoteInput(remoteInput)
            .build()
    }

    private fun getAvatar(url: String?): Bitmap {
        return try {
            Glide.with(this)
                .asBitmap()
                .load(profileImageUrlConverter.convert(url))
                .error(R.drawable.common_avatar)
                .transform(CircleCrop())
                .submit(100, 100)
                .get()
        } catch (e: Exception) {
            Glide.with(this)
                .asBitmap()
                .load(R.drawable.common_avatar)
                .submit(100, 100)
                .get()
        }
    }

    private fun restoreMessagingStyle(notificationId: Int): NotificationCompat.MessagingStyle? {
        return (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .activeNotifications
            .find { it.id == notificationId }
            ?.notification
            ?.let { NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(it) }
    }

    companion object {
        private const val SUMMARY_ID = 0
        private const val GROUP_HOOLICHAT = "io.fasthome.fenestram_messenger.HOOLICHAT"
        const val KEY_TEXT_REPLY = "KEY_TEXT_REPLY"
    }
}