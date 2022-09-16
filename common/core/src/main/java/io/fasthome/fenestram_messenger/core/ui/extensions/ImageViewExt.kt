/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.core.ui.extensions

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import coil.load
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import io.fasthome.fenestram_messenger.core.R
import io.fasthome.fenestram_messenger.util.dp

fun ImageView.loadRounded(url: String?, placeholderRes: Int? = null, radius : Float = 5.dp.toFloat()) {
    var plcRes = placeholderRes
    if(plcRes == null) {
        plcRes = R.drawable.shape_placeholder_gray
    }
    this.load(url) {
        placeholder(plcRes)
        transformations(RoundedCornersTransformation(radius))
        scale(Scale.FILL)
    }
}

fun ImageView.loadRounded(bitmap: Bitmap?, placeholderRes: Int? = null, radius : Float = 5.dp.toFloat()) {
    var plcRes = placeholderRes
    if(plcRes == null) {
        plcRes = R.drawable.shape_placeholder_gray
    }
    this.load(bitmap) {
        placeholder(plcRes)
        transformations(RoundedCornersTransformation(radius))
        scale(Scale.FILL)
    }
}

fun ImageView.loadCircle(url: String?, placeholderRes: Int? = null) {
    this.load(url) {
        placeholderRes?.let {
            placeholder(placeholderRes)
        }
        transformations(CircleCropTransformation())
    }
}

fun ImageView.loadCircle(@DrawableRes imageRes: Int?, placeholderRes: Int? = null) {
    checkNotNull(imageRes)
    this.load(imageRes) {
        placeholderRes?.let {
            placeholder(placeholderRes)
        }
        transformations(CircleCropTransformation())
    }
}