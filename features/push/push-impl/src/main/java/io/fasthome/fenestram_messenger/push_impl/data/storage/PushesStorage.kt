package io.fasthome.fenestram_messenger.push_impl.data.storage

import android.content.Context
import com.onesignal.OneSignal
import io.fasthome.fenestram_messenger.push_impl.domain.entity.DeviceTokenResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PushesStorage(
    private val context: Context,
) {
    suspend fun getDeviceToken(): DeviceTokenResult = suspendCancellableCoroutine { continuation ->
        val pushToken = OneSignal.getDeviceState()?.pushToken
        if (!continuation.isCancelled) {
            if (pushToken != null) {
                continuation.resume(DeviceTokenResult.TokenReceived(pushToken))
            } else {
                continuation.resume(DeviceTokenResult.TokenReceivingError(Throwable("PushToken is null")))
            }
        }
    }
}