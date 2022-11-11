/**
 * Created by Dmitry Popov on 09.11.2022.
 */
package io.fasthome.fenestram_messenger.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun Context.vibrate() {
    val v: Vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        v.vibrate(5)
    }
}