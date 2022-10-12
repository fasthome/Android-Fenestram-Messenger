/**
 * Created by Dmitry Popov on 19.07.2022.
 */
package io.fasthome.fenestram_messenger.util

import android.content.res.Resources
import android.graphics.Rect
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import kotlinx.coroutines.suspendCancellableCoroutine

inline fun View.onClick(crossinline action: () -> Unit) {
    setOnClickListener { action() }
}

fun View.increaseHitArea(dp: Int) {
    val increasedArea = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
    val parent = this.parent as View
    parent.post {
        val rect = Rect()
        this.getHitRect(rect)
        rect.top -= increasedArea
        rect.left -= increasedArea
        rect.bottom += increasedArea
        rect.right += increasedArea
        parent.touchDelegate = TouchDelegate(rect, this)
    }
}
val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

suspend fun View.awaitPost() = suspendCancellableCoroutine<Unit> { continuation ->
    post { continuation.resume(Unit, null) }
}

val View.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)

fun View.setOnSizeChanged(
    onHeightChanged:(height:Int) -> Unit = {},
    onWidthChanged:(width:Int) -> Unit = {},) {
    this.addOnLayoutChangeListener { v, _, _, _, _, leftWas, topWas, rightWas, bottomWas ->
        val widthWas = rightWas - leftWas
        if (v.width != widthWas) {
            onWidthChanged(v.width)
        }
        val heightWas = bottomWas - topWas
        if (v.height != heightWas) {
            onHeightChanged(v.height)
        }
    }
}
