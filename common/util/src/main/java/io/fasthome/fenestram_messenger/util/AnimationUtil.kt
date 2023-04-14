package io.fasthome.fenestram_messenger.util

import android.view.View
import android.view.animation.*
import androidx.annotation.AnimRes


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

    fun View.loadAnimation(@AnimRes animRes: Int,onEnd: () -> Unit): Animation? {
        val anim = AnimationUtils.loadAnimation(context, animRes)
        anim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                onEnd()
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

        })
        return anim
    }

    fun getAlphaAnimation(start: Float, end: Float): Animation {
        val anim: Animation = AlphaAnimation(start,end)
        anim.fillAfter = true
        anim.duration = 200
        return anim
    }
}