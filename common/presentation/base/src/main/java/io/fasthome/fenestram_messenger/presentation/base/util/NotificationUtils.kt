package io.fasthome.fenestram_messenger.presentation.base.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import io.fasthome.fenestram_messenger.presentation.base.R

fun Context.createNotificationChannel(
    channel: Channel,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val androidNotificationChannel = channel.toAndroidNotificationChannel(applicationContext)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(androidNotificationChannel)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
private fun mapImportance(importance: NotificationImportance) = when (importance) {
    NotificationImportance.None -> NotificationManager.IMPORTANCE_NONE
    NotificationImportance.Min -> NotificationManager.IMPORTANCE_MIN
    NotificationImportance.Low -> NotificationManager.IMPORTANCE_LOW
    NotificationImportance.Default -> NotificationManager.IMPORTANCE_DEFAULT
    NotificationImportance.High -> NotificationManager.IMPORTANCE_HIGH
    NotificationImportance.Max -> NotificationManager.IMPORTANCE_MAX
    NotificationImportance.Unspecified -> NotificationManager.IMPORTANCE_UNSPECIFIED
}

private enum class NotificationImportance {
    None, Min, Low, Default, High, Max, Unspecified
}

enum class Channel(
    private val importance: NotificationImportance,
    @StringRes private val title: Int,
    @StringRes private val description: Int,
) {
    Push(
        importance = NotificationImportance.Default,
        title = R.string.common_notification_channel_title_push,
        description = R.string.common_notification_channel_description_push,
    );

    val id get() = name

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun toAndroidNotificationChannel(context: Context) = NotificationChannel(
        id,
        context.getString(title),
        mapImportance(importance)
    ).apply { this.description = context.getString(this@Channel.description) }
}