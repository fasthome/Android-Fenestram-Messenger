/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.core.ui.extensions

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import io.fasthome.fenestram_messenger.util.dp

fun ImageView.loadRounded(url: String?, placeholderRes: Int? = null, radius : Float = 5.dp.toFloat()) {
    this.load(url) {
        placeholderRes?.let {
            placeholder(placeholderRes)
        }
        transformations(RoundedCornersTransformation(radius))
    }
}

fun ImageView.loadRounded(bitmap: Bitmap?, placeholderRes: Int? = null, radius : Float = 5.dp.toFloat()) {
    this.load(bitmap) {
        placeholderRes?.let {
            placeholder(placeholderRes)
        }
        transformations(RoundedCornersTransformation(radius))
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