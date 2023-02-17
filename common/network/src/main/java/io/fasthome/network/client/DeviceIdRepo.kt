package io.fasthome.network.client

import android.content.ContentResolver
import android.provider.Settings
import io.fasthome.fenestram_messenger.util.callForResult

class DeviceIdRepo(
    private val contentResolver : ContentResolver
) {

    fun getAndroidDeviceId() = callForResult {
        Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID)
    }

}