package io.fasthome.fenestram_messenger.util

import android.view.animation.Animation
import android.view.animation.ScaleAnimation


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
}