package io.fasthome.fenestram_messenger.push_impl.data.storage

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.firebase.messaging.FirebaseMessaging
import io.fasthome.fenestram_messenger.push_impl.domain.entity.DeviceTokenResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PushesStorage(
    private val context: Context,
) {

    suspend fun getDeviceToken(): DeviceTokenResult = suspendCancellableCoroutine { continuation ->
        if (!isGooglePlayServicesAvailable()) {
            continuation.resume(DeviceTokenResult.PlayServicesNotAvailable)
            return@suspendCancellableCoroutine
        }

        FirebaseMessaging.getInstance().token
            .addOnCanceledListener { continuation.cancel() }
            .addOnCompleteListener { task ->
                if (continuation.isCancelled) {
                    return@addOnCompleteListener
                }
                if (task.isSuccessful) {
                    continuation.resume(DeviceTokenResult.TokenReceived(task.result!!))
                } else {
                    continuation.resume(DeviceTokenResult.TokenReceivingError(task.exception!!))
                }
            }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val availableResult =
            GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context)
        return availableResult == ConnectionResult.SUCCESS
    }
}