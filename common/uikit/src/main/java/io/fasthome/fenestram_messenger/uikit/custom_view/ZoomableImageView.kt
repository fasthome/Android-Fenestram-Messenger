package io.fasthome.fenestram_messenger.uikit.custom_view

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView


class ZoomableImageView : AppCompatImageView, View.OnTouchListener,
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    //shared constructing
    private var scaleDetector: ScaleGestureDetector? = null
    private var gestureDetector: GestureDetector? = null
    private var matrixGeneral: Matrix? = null
    private var onDownSwipe: (() -> Unit)? = null
    private var onAlphaChanged: ((alpha: Float) -> Unit)? = null
    private val canSwipe: Boolean
        get() = onDownSwipe != null
    private var matrixValues: FloatArray? = null
    private var mode = NONE

    // Scales
    private var saveScale = 1f
    private var minScale = MIN_SCALE
    private var maxScale = MAX_SCALE

    // view dimensions
    private var origWidth = 0f
    private var origHeight = 0f
    private var viewWidth = 0
    private var viewHeight = 0
    private var lastPoint = PointF()
    private var startPoint = PointF()
    private var startY = 0f
    private var dY = 0f
    private var centerY = 0f

    constructor(context: Context) : super(context) {
        sharedConstructing(context)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        sharedConstructing(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!,
        attrs,
        defStyleAttr)

    private fun sharedConstructing(context: Context) {
        super.setClickable(true)
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
        matrixGeneral = Matrix()
        matrixValues = FloatArray(9)
        imageMatrix = matrixGeneral
        scaleType = ScaleType.MATRIX
        gestureDetector = GestureDetector(context, this)
        setOnTouchListener(this)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = detector.scaleFactor
            val prevScale = saveScale
            saveScale *= mScaleFactor
            if (saveScale > maxScale) {
                saveScale = maxScale
                mScaleFactor = maxScale / prevScale
            } else if (saveScale < minScale) {
                saveScale = minScale
                mScaleFactor = minScale / prevScale
            }
            if (origWidth * saveScale <= viewWidth
                || origHeight * saveScale <= viewHeight
            ) {
                matrixGeneral!!.postScale(mScaleFactor, mScaleFactor, viewWidth / 2.toFloat(),
                    viewHeight / 2.toFloat())
            } else {
                matrixGeneral!!.postScale(mScaleFactor, mScaleFactor,
                    detector.focusX, detector.focusY)
            }
            fixTranslation()
            return true
        }
    }

    private fun fitToScreen() {
        saveScale = 1f
        val scale: Float
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) return
        val imageWidth = drawable.intrinsicWidth
        val imageHeight = drawable.intrinsicHeight
        val scaleX = viewWidth.toFloat() / imageWidth.toFloat()
        val scaleY = viewHeight.toFloat() / imageHeight.toFloat()
        scale = scaleX.coerceAtMost(scaleY)
        matrixGeneral!!.setScale(scale, scale)

        // Center the image
        var redundantYSpace = (viewHeight.toFloat()
                - scale * imageHeight.toFloat())
        var redundantXSpace = (viewWidth.toFloat()
                - scale * imageWidth.toFloat())
        redundantYSpace /= 2.toFloat()
        redundantXSpace /= 2.toFloat()
        matrixGeneral!!.postTranslate(redundantXSpace, redundantYSpace)
        origWidth = viewWidth - 2 * redundantXSpace
        origHeight = viewHeight - 2 * redundantYSpace
        imageMatrix = matrixGeneral
        startY = y
    }

    fun fixTranslation() {
        matrixGeneral!!.getValues(matrixValues) //put matrix values into a float array so we can analyze
        val transX =
            matrixValues!![Matrix.MTRANS_X] //get the most recent translation in x direction
        val transY =
            matrixValues!![Matrix.MTRANS_Y] //get the most recent translation in y direction
        val fixTransX = getFixTranslation(transX, viewWidth.toFloat(), origWidth * saveScale)
        val fixTransY = getFixTranslation(transY, viewHeight.toFloat(), origHeight * saveScale)
        if (fixTransX != 0f || fixTransY != 0f) matrixGeneral!!.postTranslate(fixTransX, fixTransY)
    }

    private fun getFixTranslation(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float
        if (contentSize <= viewSize) { // case: NOT ZOOMED
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else { //CASE: ZOOMED
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }
        if (trans < minTrans) { // negative x or y translation (down or to the right)
            return -trans + minTrans
        }
        if (trans > maxTrans) { // positive x or y translation (up or to the left)
            return -trans + maxTrans
        }
        return 0F
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) {
            0F
        } else delta
    }

    fun setOnSwipeDownListener(onSwipe: (() -> Unit)?) {
        onDownSwipe = onSwipe
    }

    fun setOnAlphaChangedListener(onChanged: ((alpha: Float) -> Unit)?) {
        onAlphaChanged = onChanged
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (saveScale == 1f) {
            fitToScreen()
        }
    }

    /*
        Ontouch
     */
    override fun onTouch(view: View?, event: MotionEvent): Boolean {
        if (view == null) return false
        scaleDetector!!.onTouchEvent(event)
        gestureDetector!!.onTouchEvent(event)
        val currentPoint = PointF(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastPoint.set(currentPoint)
                startPoint.set(lastPoint)
                mode = DRAG
                dY = view.y - event.rawY
                centerY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                if (saveScale == SWIPE_SCALE && canSwipe) {
                    view.animate()
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()

                    val newAlpha = if (event.rawY < centerY) event.rawY / centerY else centerY / event.rawY
                    view.alpha = newAlpha
                    onAlphaChanged?.invoke(newAlpha)
                }
                val dx = currentPoint.x - lastPoint.x
                val dy = currentPoint.y - lastPoint.y
                val fixTransX = getFixDragTrans(dx, viewWidth.toFloat(), origWidth * saveScale)
                val fixTransY = getFixDragTrans(dy, viewHeight.toFloat(), origHeight * saveScale)
                matrixGeneral!!.postTranslate(fixTransX, fixTransY)
                fixTranslation()
                lastPoint[currentPoint.x] = currentPoint.y
            }
            MotionEvent.ACTION_POINTER_UP -> mode = NONE

            MotionEvent.ACTION_UP -> {
                if (canSwipe) {
                    if (saveScale == SWIPE_SCALE) {
                        val middleTopCenter = centerY / 4
                        if (event.rawY !in centerY - middleTopCenter..centerY + middleTopCenter) {
                            onDownSwipe?.invoke()
                        }
                    }
                    view.animate()
                        .y(startY)
                        .setDuration(100)
                        .alpha(1f)
                        .start()
                    onAlphaChanged?.invoke(1f)
                }
                if(saveScale < SWIPE_SCALE) {
                    view.animate()
                        .y(startY)
                        .setDuration(100)
                        .alpha(1f)
                        .withEndAction {
                        fitToScreen()
                    }
                        .start()
                    onAlphaChanged?.invoke(1f)

                }
            }
        }
        imageMatrix = matrixGeneral
        return false
    }

    /*
        GestureListener
     */
    override fun onDown(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {}
    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        vx: Float,
        vy: Float,
    ): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {}
    override fun onFling(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float,
    ): Boolean {
        return false
    }

    /*
        onDoubleTap
     */
    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        if(saveScale == SWIPE_SCALE) {
            val mScaleFactor = 2f
            saveScale *= mScaleFactor
            if (origWidth * saveScale <= viewWidth
                || origHeight * saveScale <= viewHeight
            ) {
                matrixGeneral!!.postScale(mScaleFactor, mScaleFactor, viewWidth / 2.toFloat(),
                    viewHeight / 2.toFloat())
            } else {
                matrixGeneral!!.postScale(mScaleFactor, mScaleFactor,
                    motionEvent.x, motionEvent.y)
            }
            fixTranslation()
        } else {
            fitToScreen()
        }
        return true
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
        return false
    }

    companion object {

        // Image States
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2

        const val SWIPE_SCALE = 1f
        const val MIN_SCALE = -2f
        const val MAX_SCALE = 4f
    }
}