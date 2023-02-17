package io.fasthome.fenestram_messenger.uikit.custom_view.bottomNav

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout.LayoutParams
import androidx.annotation.Nullable
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import io.fasthome.fenestram_messenger.uikit.R
import io.fasthome.fenestram_messenger.util.dp
import kotlinx.coroutines.*


class TabIndicator : View {

    companion object {
        private const val TAB_HEIGHT = 8
        private const val TAB_WIDTH = 35
    }

    private val tabScope = CoroutineScope(Dispatchers.Main + Job())

    init {
        y = 0f
        val drawable: Drawable? =
            VectorDrawableCompat.create(
                resources,
                R.drawable.ic_bottom_nav_indicator,
                context.theme
            )
        layoutParams = LayoutParams(TAB_WIDTH.dp, TAB_HEIGHT.dp, Gravity.BOTTOM)
        background = drawable
    }

    fun setNewPosition(bottomNavItemView: BottomNavigationItemView, withAnim: Boolean) {
        val newPosX = bottomNavItemView.x + bottomNavItemView.width / 2 - width / 2
        if (withAnim) {
            val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.6f)
            val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.6f)
            val moveX = ObjectAnimator.ofFloat(this, "x", newPosX)

            val animSet = AnimatorSet()
            animSet.playTogether(scaleY, moveX, scaleX)
            animSet.duration = 200
            animSet.start()
            tabScope.launch {
                delay(100)
                CoroutineScope(Dispatchers.Main).launch {
                    val secondAnimSet = AnimatorSet()
                    val backScaleY = ObjectAnimator.ofFloat(this@TabIndicator, "scaleY", 1f)
                    val backScaleX = ObjectAnimator.ofFloat(this@TabIndicator, "scaleX", 1f)
                    secondAnimSet.playTogether(backScaleY, backScaleX)
                    secondAnimSet.start()
                }
            }
        } else {
            x = newPosX
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(
        context, attrs
    ) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
    }

}