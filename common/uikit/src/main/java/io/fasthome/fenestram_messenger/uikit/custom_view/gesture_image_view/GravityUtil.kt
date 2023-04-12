package io.fasthome.fenestram_messenger.uikit.custom_view.gesture_image_view

import android.graphics.Matrix
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.Gravity

object GravityUtil {
    private val tmpMatrix = Matrix()
    private val tmpRectF = RectF()
    private val tmpRect1 = Rect()
    private val tmpRect2 = Rect()

    fun getImagePosition(state: State, settings: GeastureImageSettings, out: Rect) {
        state[tmpMatrix]
        getImagePosition(tmpMatrix, settings, out)
    }

    fun getImagePosition(matrix: Matrix, settings: GeastureImageSettings, out: Rect) {
        tmpRectF.set(0f, 0f, settings.imageWidth, settings.imageHeight)

        matrix.mapRect(tmpRectF)

        val width = Math.round(tmpRectF.width())
        val height = Math.round(tmpRectF.height())

        tmpRect1.set(0, 0, settings.viewportWidth, settings.viewportHeight)
        Gravity.apply(Gravity.CENTER, width, height, tmpRect1, out)
    }

    fun getMovementAreaPosition(settings: GeastureImageSettings, out: Rect) {
        tmpRect1.set(0, 0, settings.viewportWidth, settings.viewportHeight)
        Gravity.apply(Gravity.CENTER, settings.viewportWidth, settings.viewportHeight, tmpRect1, out)
    }

    fun getDefaultPivot(settings: GeastureImageSettings, out: Point) {
        getMovementAreaPosition(settings, tmpRect2)
        Gravity.apply(Gravity.CENTER, 0, 0, tmpRect2, tmpRect1)
        out.set(tmpRect1.left, tmpRect1.top)
    }
}