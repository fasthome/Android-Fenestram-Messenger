package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.LinearInterpolator
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.fragment.app.Fragment

fun View.animateAlpha(alpha: Float, duration: Long = 300): ViewPropertyAnimator = animate().apply {
    interpolator = LinearInterpolator()
    this.duration = duration
    alpha(alpha)
    start()
}

fun Fragment.animateKeyboard(viewsToTranslate: List<View>, viewsToFocus: List<View>) {
    WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    viewsToTranslate.forEach {
        ViewCompat.setWindowInsetsAnimationCallback(it, TranslateInsetsAnimationCallback(it))
    }
    viewsToFocus.forEach {
        ViewCompat.setWindowInsetsAnimationCallback(it, ControlFocusInsetsAnimationCallback(it))
    }
}

class TranslateInsetsAnimationCallback(
    private val view: View,
    dispatchMode: Int = DISPATCH_MODE_STOP
) : WindowInsetsAnimationCompat.Callback(dispatchMode) {

    private var viewMarginBottom = 0

    init {
        viewMarginBottom = view.marginBottom
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        val typesInset = insets.getInsets(WindowInsetsCompat.Type.ime())
        val otherInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())

        val diff = Insets.subtract(typesInset, otherInset).let {
            Insets.max(it, Insets.NONE)
        }
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMargins(
                bottom = viewMarginBottom + diff.bottom
            )
        }
        return insets
    }
}

class ControlFocusInsetsAnimationCallback(
    private val view: View,
    dispatchMode: Int = DISPATCH_MODE_STOP
) : WindowInsetsAnimationCompat.Callback(dispatchMode) {

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: List<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        return insets
    }

    override fun onStart(
        animation: WindowInsetsAnimationCompat,
        bounds: WindowInsetsAnimationCompat.BoundsCompat
    ): WindowInsetsAnimationCompat.BoundsCompat {
        if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            view.post {
                checkFocus()
            }
        }
        return super.onStart(animation, bounds)
    }

    private fun checkFocus() {
        val imeVisible = ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime()) == true
        if (imeVisible && view.rootView.findFocus() == null) {
            view.requestFocus()
        } else if (!imeVisible && view.isFocused) {
            view.clearFocus()
        }
    }
}
