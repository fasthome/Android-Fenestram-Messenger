/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.util.android

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat

fun Resources.color(@ColorRes colorRes: Int): Int =
    ResourcesCompat.getColor(this, colorRes, null)

fun Context.color(@ColorRes colorRes: Int): Int =
    this.resources.color(colorRes)

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable =
    AppCompatResources.getDrawable(this, drawableRes)!!

fun Context.colorStateList(@ColorRes colorRes: Int): ColorStateList =
    AppCompatResources.getColorStateList(this, colorRes)

fun Drawable.setColor(colorInt: Int) = DrawableCompat.setTint(DrawableCompat.wrap(this), colorInt)


fun Drawable.setBackgroundColor(color: Int?) {
    val drawable = this as GradientDrawable
    drawable.mutate()
    if (color != null) {
        drawable.setColor(color)
    } else drawable.color = null
}

fun Drawable.setStroke(color: Int, width: Int = 1, dashWidth: Float = 0f, dashGap: Float = 0f) {
    val drawable = this as GradientDrawable
    drawable.mutate()
    drawable.setStroke(width, color, dashWidth, dashGap)
}
