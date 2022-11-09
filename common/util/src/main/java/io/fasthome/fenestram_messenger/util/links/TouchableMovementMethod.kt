package io.fasthome.fenestram_messenger.util.links

import android.os.Handler
import android.os.Looper
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.widget.TextView

class TouchableMovementMethod : LinkMovementMethod() {

    var pressedSpan: TouchableBaseSpan? = null
        private set

    override fun onTouchEvent(
        textView: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            pressedSpan = getPressedSpan(textView, spannable, event)

            if (pressedSpan != null) {
                pressedSpan!!.isTouched = true
                touched = true

                Handler(Looper.getMainLooper()).postDelayed({
                    if (touched && pressedSpan != null) {
                        if (textView.isHapticFeedbackEnabled)
                            textView.isHapticFeedbackEnabled = true
                        textView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                        pressedSpan!!.onLongClick(textView)
                        pressedSpan!!.isTouched = false
                        pressedSpan = null

                        Selection.removeSelection(spannable)
                    }
                }, 500)

                Selection.setSelection(
                    spannable, spannable.getSpanStart(pressedSpan),
                    spannable.getSpanEnd(pressedSpan)
                )
            }
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            val touchedSpan = getPressedSpan(textView, spannable, event)

            if (pressedSpan != null && pressedSpan != touchedSpan) {
                pressedSpan!!.isTouched = false
                pressedSpan = null
                touched = false

                Selection.removeSelection(spannable)
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (pressedSpan != null) {
                pressedSpan!!.onClick(textView)
                pressedSpan!!.isTouched = false
                pressedSpan = null

                Selection.removeSelection(spannable)
            }
        } else {
            if (pressedSpan != null) {
                pressedSpan!!.isTouched = false
                touched = false

                super.onTouchEvent(textView, spannable, event)
            }

            pressedSpan = null

            Selection.removeSelection(spannable)
        }

        return true
    }

    private fun getPressedSpan(
        widget: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): TouchableBaseSpan? {

        var x = event.x.toInt()
        var y = event.y.toInt()

        x -= widget.totalPaddingLeft
        y -= widget.totalPaddingTop

        x += widget.scrollX
        y += widget.scrollY

        val layout = widget.layout
        val line = layout.getLineForVertical(y)

        val off = try {
            layout.getOffsetForHorizontal(line, x.toFloat())
        } catch (e: IndexOutOfBoundsException) {
            return null
        }

        val end = layout.getLineEnd(line)

        if (off != end && off != end - 1) {
            val link = spannable.getSpans(off, off, TouchableBaseSpan::class.java)

            if (link.isNotEmpty())
                return link[0]
        }

        return null
    }

    companion object {

        private var sInstance: TouchableMovementMethod? = null
        var touched = false

        val instance: MovementMethod
            get() {
                if (sInstance == null)
                    sInstance = TouchableMovementMethod()

                return sInstance!!
            }
    }
}