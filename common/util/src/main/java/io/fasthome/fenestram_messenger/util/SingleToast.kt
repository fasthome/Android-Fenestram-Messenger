package io.fasthome.fenestram_messenger.util

import android.content.Context
import android.widget.Toast

object SingleToast {
    private var mToast: Toast? = null
    fun show(context: Context?, text: String?, duration: Int) {
        mToast?.cancel()
        mToast = Toast.makeText(context, text, duration)
        mToast?.show()
    }
}