/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.util.android

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat

fun Resources.color(@ColorRes colorRes: Int): Int =
    ResourcesCompat.getColor(this, colorRes, null)

fun Context.color(@ColorRes colorRes: Int): Int =
    this.resources.color(colorRes)

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable =
    AppCompatResources.getDrawable(this, drawableRes)!!

fun Context.colorStateList(@ColorRes colorRes: Int): ColorStateList =
    AppCompatResources.getColorStateList(this, colorRes)

fun Drawable.setColor(colorInt: Int) = DrawableCompat.setTint(DrawableCompat.wrap(this), colorInt)

fun View.setDrawableBackgroundStroke(colorInt: Int, dashWidth: Float = 0f, dashGap: Float = dashWidth, width: Int = 2) {
    val drawable = background as GradientDrawable
    drawable.mutate()
    drawable.setStroke(width, colorInt, dashWidth, dashGap)
}

fun View.setDrawableBackgroundColor(colorInt: Int?) {
    val drawable = background as GradientDrawable
    drawable.color = colorInt?.let { ColorStateList.valueOf(it) }
}

fun TextView.setDrawableRight(@DrawableRes drawableRes: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)
}

fun TextView.setDrawableRightColor(colorInt: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(colorInt))
}
