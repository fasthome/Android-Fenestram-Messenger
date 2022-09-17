package io.fasthome.fenestram_messenger.util

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

class RoundedCornersOutlineProvider(private val radius: Float) : ViewOutlineProvider() {
    override fun getOutline(view: View?, outline: Outline?) {
        if (outline == null || view == null) return

        outline.setRoundRect(0, 0, view.width, view.height, radius)
    }
}
