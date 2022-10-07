/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.push_impl.data

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.fasthome.fenestram_messenger.auth_api.AuthFeature
import io.fasthome.fenestram_messenger.core.coroutines.DispatchersProvider
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

class FirebasePushService : FirebaseMessagingService() {

    private val pushRepo by inject<PushRepo>()
    private val remoteMessagesStorage by inject<RemoteMessagesStorage>()
    private val appFeature by inject<AppFeature>()
    private val authFeature by inject<AuthFeature>()

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
        val remoteNotification = message.notification ?: return

        val notificationId = remoteMessagesStorage.nextNotificationId()

        val activityIntent = Intent(this, appFeature.startActivityClazz.java)
        activityIntent.putExtra(KEY_FROM_NOTIFICATION, true)
        message.data.forEach { (k, v) -> activityIntent.putExtra(k, v) }
        val contentIntent = PendingIntent.getActivity(
            this,
            notificationId,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE,
        )

        createNotificationChannel(Channel.Push)
        val notification = NotificationCompat.Builder(this, Channel.Push.id)
            .setSmallIcon(R.drawable.ic_message)
            .setColor(resources.color(R.color.blue))
            .setTicker(remoteNotification.title)
            .setContentTitle(remoteNotification.title)
            .setContentText(remoteNotification.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(remoteNotification.body))
            .setShowWhen(true)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

    companion object {
        const val KEY_FROM_NOTIFICATION = "key_from_notification"
    }
}