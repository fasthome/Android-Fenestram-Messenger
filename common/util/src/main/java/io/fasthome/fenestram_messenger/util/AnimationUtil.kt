package io.fasthome.fenestram_messenger.util

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation


object AnimationUtil {
    fun getScaleAnimation(startScale: Float, endScale: Float): Animation {
        val anim: Animation = ScaleAnimation(
            startScale, endScale,  // Start and end values for the X axis scaling
            startScale, endScale,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f) // Pivot point of Y scaling
        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = 300
        return anim
    }

    fun View.getTranslateYAnimation(endY: Float):Animation {
        val anim: Animation = TranslateAnimation(
            this.x, this.x,this.y,endY
        )
        anim.fillAfter = true
        anim.duration = 300
        return anim
    }

    fun getAlphaAnimation(start: Float, end: Float): Animation {
        val anim: Animation = AlphaAnimation(start,end)
        anim.fillAfter = true
        anim.duration = 200
        return anim
    }
}