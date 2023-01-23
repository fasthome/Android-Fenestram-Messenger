package io.fasthome.fenestram_messenger.uikit.custom_view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewWithToggle : RecyclerView {
    var isCanScrolling = true

    override fun computeVerticalScrollRange(): Int {
        return if (isCanScrolling) super.computeVerticalScrollRange() else 0
    }

    override fun computeHorizontalScrollRange(): Int {
        return if (isCanScrolling) super.computeHorizontalScrollRange() else 0
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return if (isCanScrolling) super.onInterceptTouchEvent(e) else false
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(
        context, attrs) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle) {
    }
}