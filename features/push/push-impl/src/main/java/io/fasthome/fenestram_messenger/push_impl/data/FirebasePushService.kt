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
import io.fasthome.fenestram_messenger.push_impl.data.service.mapper.NotificationDataMapper
import io.fasthome.fenestram_messenger.push_impl.data.storage.RemoteMessagesStorage
import io.fasthome.fenestram_messenger.push_impl.domain.entity.NotificationData
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

    override fun onNewToken(token: String) {
        initPushesJob = scope.launch {
            if (authFeature.isUserAuthorized().getOrDefault(false)) {
                pushRepo.sendPushToken()
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notificationData = NotificationDataMapper().getNotificationData(message.data)
        val notificationId = notificationData.chatId ?: remoteMessagesStorage.nextNotificationId()

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
        val mePerson = Person.Builder().setName(resources.getString(R.string.my_user_name)).build()
        val notificationMessage = buildMessage(notificationData)

        val messagingStyle = restoreMessagingStyle(notificationId)?.addMessage(notificationMessage)
            ?: if (notificationData.isGroup) {
                NotificationCompat.MessagingStyle(mePerson)
                    .addMessage(notificationMessage)
                    .setConversationTitle(notificationData.chatName)
                    .setGroupConversation(true)
            } else {
                NotificationCompat.MessagingStyle(mePerson)
                    .addMessage(notificationMessage)
            }

        val notification = NotificationCompat.Builder(this, Channel.Push.id).also {
            if (notificationData.isGroup) it.setLargeIcon(getAvatar(notificationData.chatAvatar))
        }
            .setSmallIcon(R.drawable.ic_message)
            .setColor(resources.color(R.color.background))
            .setStyle(messagingStyle)
            .setShowWhen(true)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setGroup(GROUP_HOOLICHAT)
            .addAction(buildAction(replyIntent))
            .build()

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

    private fun buildMessage(notificationData: NotificationData): NotificationCompat.MessagingStyle.Message {
        val iconBitmap = getAvatar(notificationData.userAvatar)

        return NotificationCompat.MessagingStyle.Message(
            notificationData.text,
            ZonedDateTime.now().toInstant().toEpochMilli(),
            Person.Builder()
                .setKey(notificationData.userId)
                .setName(notificationData.userName)
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