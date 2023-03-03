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

    var context: Context

    fun mainActive(): Int
    fun backgroundColor(): Int
    fun backgroundGeometry() : Drawable

    fun text0Color(): Int
    fun text1Color(): Int
    fun stroke2Color(): Int
    fun bg0Color(): Int
    fun bg02Color(): Int
    fun bg03Color(): Int
    fun bg1Color(): Int
    fun bg2Color(): Int
    fun bg3Color(): Int
    fun buttonInactiveColor(): Int
    fun redColor(): Int = context.color(R.color.red)

    //Main
    fun navigationViewBackground() : Int

    //Profile
    fun gradientDrawable(): Drawable
    fun shapeBg02_5dp(): Drawable
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

    // Conversation
    fun bgGradient_Top10_BottomLeft10(): Drawable
    fun shapeBg3_Top10_BottomRight10(): Drawable
}

class LightTheme : Theme {

    override lateinit var context: Context

    override fun mainActive(): Int {
        return context.color(R.color.main_active)
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

    override fun stroke2Color(): Int {
        return context.color(R.color.stroke_2)
    }

    override fun bg0Color(): Int {
        return context.color(R.color.bg_0)
    }

    override fun bg02Color(): Int {
        return context.color(R.color.bg_02)
    }

    override fun bg03Color(): Int {
        return context.color(R.color.bg_03)
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

    override fun buttonInactiveColor(): Int {
        return context.color(R.color.button_inactive)
    }

    override fun navigationViewBackground(): Int {
        return R.drawable.background_bottom_navigation
    }

    override fun gradientDrawable(): Drawable {
        return context.drawable(R.drawable.bg_profile_gradient)
    }

    override fun shapeBg02_5dp(): Drawable {
        return context.drawable(R.drawable.shape_bg_02_5dp)
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

    override fun bgGradient_Top10_BottomLeft10(): Drawable {
        return context.drawable(R.drawable.shape_gradient_top10_bottom_left10)
    }

    override fun shapeBg3_Top10_BottomRight10(): Drawable {
        return context.drawable(R.drawable.shape_top10_bottom_right10)
    }

    override fun id(): Int = 1

}

class DarkTheme : Theme {

    override lateinit var context: Context

    override fun mainActive(): Int {
        return context.color(R.color.main_active)
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

    override fun stroke2Color(): Int {
        return context.color(R.color.stroke_2_dark)
    }

    override fun bg0Color(): Int {
        return context.color(R.color.bg_0_dark)
    }

    override fun bg02Color(): Int {
        return context.color(R.color.bg_02_dark)
    }

    override fun bg03Color(): Int {
        return context.color(R.color.bg_03_dark)
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

    override fun buttonInactiveColor(): Int {
        return context.color(R.color.button_inactive_dark)
    }

    override fun navigationViewBackground(): Int {
        return R.drawable.background_bottom_navigation_dark
    }

    override fun gradientDrawable(): Drawable {
        return context.drawable(R.drawable.bg_profile_gradient_night)
    }

    override fun shapeBg02_5dp(): Drawable {
        return context.drawable(R.drawable.shape_bg_02_5dp_dark)
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

    override fun bgGradient_Top10_BottomLeft10(): Drawable {
        return context.drawable(R.drawable.shape_gradient_top10_bottom_left10_dark)
    }

    override fun shapeBg3_Top10_BottomRight10(): Drawable {
        return context.drawable(R.drawable.shape_top10_bottom_right10_dark)
    }

    override fun id(): Int = 2

}