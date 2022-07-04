package io.fasthome.fenestram_messenger.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children

class ProvideInsetsCoordinatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = androidx.coordinatorlayout.R.attr.coordinatorLayoutStyle,
) : CoordinatorLayout(context, attrs, defStyleRes) {

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        children.forEach { child ->
            dispatchInsets(child, insets)
        }
        return insets.consumeSystemWindowInsets()
    }

    private fun dispatchInsets(view: View, insets: WindowInsets) {
        view.dispatchApplyWindowInsets(insets)

        if (view is ViewGroup) {
            view.children.forEach { child ->
                dispatchInsets(child, insets)
            }
        }
    }
}