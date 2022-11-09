package io.fasthome.fenestram_messenger.util.links

import android.os.Handler
import android.os.Looper
import android.text.style.ClickableSpan
import android.view.View

abstract class TouchableBaseSpan : ClickableSpan() {

    var isTouched = false

    override fun onClick(widget: View) {
        Handler(Looper.getMainLooper()).postDelayed(
            { TouchableMovementMethod.touched = false },
            500
        )
    }

    open fun onLongClick(widget: View) {
        Handler(Looper.getMainLooper()).postDelayed(
            { TouchableMovementMethod.touched = false },
            500
        )
    }
}