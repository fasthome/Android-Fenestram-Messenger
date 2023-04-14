package io.fasthome.fenestram_messenger.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object SingleToast {
    private var mToast: Toast? = null
    fun show(context: Context?, text: String?, duration: Int) {
        mToast?.cancel()
        mToast = Toast.makeText(context, text, duration)
        mToast?.show()
    }

    fun show(context: Context?,@StringRes textRes: Int, duration: Int) {
        show(context, context?.getString(textRes), duration)
    }
}