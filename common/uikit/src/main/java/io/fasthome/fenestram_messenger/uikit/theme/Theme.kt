package io.fasthome.fenestram_messenger.uikit.theme

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.TextView
import com.dolatkia.animatedThemeManager.AppTheme
import io.fasthome.fenestram_messenger.uikit.R
import io.fasthome.fenestram_messenger.util.android.color
import io.fasthome.fenestram_messenger.util.android.colorStateList
import io.fasthome.fenestram_messenger.util.android.drawable

interface Theme : AppTheme {
    fun setContext(context: Context)

    fun backgroundColor(): Int
    fun backgroundGeometry() : Drawable

    fun text0Color(): Int
    fun text1Color(): Int
    fun bg0Color(): Int
    fun bg1Color(): Int
    fun bg2Color(): Int
    fun bg3Color(): Int

    //Main
    fun navigationViewBackground() : Int

    //Profile
    fun gradientDrawable(): Drawable
    fun shapeBg2_20dp(): Drawable
    fun shapeBg3_20dp(): Drawable

    //AuthAd
    fun logoColor(): Int
    fun boxBackgroundColor(): Int
    fun boxStrokeColor(): ColorStateList
    fun buttonDrawableRes(): Int
    fun endIconTint(): ColorStateList

    // Messenger
    fun logoGradient(textView: TextView)

    fun shapeBg2_10dp(): Drawable
}

class LightTheme : Theme {

    private lateinit var context: Context

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun backgroundColor(): Int {
        return context.color(R.color.background)
    }

    override fun backgroundGeometry(): Drawable {
        return context.drawable(R.drawable.layout_bg1)
    }

    override fun text0Color(): Int {
        return context.color(R.color.text_0)
    }

    override fun text1Color(): Int {
        return context.color(R.color.text_1)
    }

    override fun bg0Color(): Int {
        return context.color(R.color.bg_0)
    }

    override fun bg1Color(): Int {
        return context.color(R.color.bg_1)
    }

    override fun bg2Color(): Int {
        return context.color(R.color.bg_2)
    }

    override fun bg3Color(): Int {
        return context.color(R.color.bg_3)
    }

    override fun navigationViewBackground(): Int {
        return R.drawable.background_bottom_navigation
    }

    override fun gradientDrawable(): Drawable {
        return context.drawable(R.drawable.bg_profile_gradient)
    }

    override fun shapeBg2_20dp(): Drawable {
        return context.drawable(R.drawable.shape_bg_2_20dp)
    }

    override fun shapeBg3_20dp(): Drawable {
        return context.drawable(R.drawable.shape_bg_3_20dp)
    }

    override fun logoColor(): Int {
        return context.color(R.color.logo_tfn)
    }

    override fun boxBackgroundColor(): Int {
        return context.color(R.color.white)
    }

    override fun boxStrokeColor(): ColorStateList {
        return context.colorStateList(R.color.selector_text_input)
    }

    override fun buttonDrawableRes(): Int {
        return R.drawable.rounded_violet_button
    }

    override fun endIconTint(): ColorStateList {
        return context.colorStateList(R.color.selector_show_password)
    }

    override fun logoGradient(textView: TextView) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, textView.textSize, intArrayOf(
                Color.parseColor("#9F86C7"),
                Color.parseColor("#77BDED"),
            ), null, TileMode.CLAMP
        )

        textView.paint.shader = textShader
    }

    override fun shapeBg2_10dp(): Drawable {
        return context.drawable(R.drawable.shape_bg_2_10dp)
    }

    override fun id(): Int = 1

}

class DarkTheme : Theme {

    private lateinit var context: Context

    override fun setContext(context: Context) {
        this.context = context
    }

    override fun backgroundColor(): Int {
        return context.color(R.color.dark1)
    }

    override fun backgroundGeometry(): Drawable {
        return context.drawable(R.drawable.layout_bg1)
    }

    override fun text0Color(): Int {
        return context.color(R.color.text_0_dark)
    }

    override fun text1Color(): Int {
        return context.color(R.color.text_1_dark)
    }

    override fun bg0Color(): Int {
        return context.color(R.color.bg_0_dark)
    }

    override fun bg1Color(): Int {
        return context.color(R.color.bg_1_dark)
    }

    override fun bg2Color(): Int {
        return context.color(R.color.bg_2_dark)
    }

    override fun bg3Color(): Int {
        return context.color(R.color.bg_3_dark)
    }

    override fun navigationViewBackground(): Int {
        return R.drawable.background_bottom_navigation_dark
    }

    override fun gradientDrawable(): Drawable {
        return context.drawable(R.drawable.bg_profile_gradient_night)
    }

    override fun shapeBg2_20dp(): Drawable {
        return context.drawable(R.drawable.shape_bg_2_20dp_dark)
    }

    override fun shapeBg3_20dp(): Drawable {
        return context.drawable(R.drawable.shape_bg_3_20dp_dark)
    }

    override fun logoColor(): Int {
        return context.color(R.color.white)
    }

    override fun boxBackgroundColor(): Int {
        return context.color(R.color.bg_0_dark)
    }

    override fun boxStrokeColor(): ColorStateList {
        return context.colorStateList(R.color.selector_text_input_dark)
    }

    override fun buttonDrawableRes(): Int {
        return R.drawable.rounded_violet_button_dark
    }

    override fun endIconTint(): ColorStateList {
        return context.colorStateList(R.color.selector_show_password_dark)
    }

    override fun logoGradient(textView: TextView) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, textView.textSize, intArrayOf(
                Color.parseColor("#4C3E77"),
                Color.parseColor("#3D5085"),
            ), null, TileMode.CLAMP
        )

        textView.paint.shader = textShader
    }

    override fun shapeBg2_10dp(): Drawable {
        return context.drawable(R.drawable.shape_bg_2_10dp_dark)
    }

    override fun id(): Int = 2

}