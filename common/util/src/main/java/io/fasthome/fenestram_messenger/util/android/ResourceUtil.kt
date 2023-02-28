/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.util.android

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat

fun Resources.color(@ColorRes colorRes: Int): Int =
    ResourcesCompat.getColor(this, colorRes, null)

fun Context.color(@ColorRes colorRes: Int): Int =
    this.resources.color(colorRes)

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable =
    AppCompatResources.getDrawable(this, drawableRes)!!

fun Context.colorStateList(@ColorRes colorRes: Int): ColorStateList =
    AppCompatResources.getColorStateList(this, colorRes)