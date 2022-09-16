/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.core.ui.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import coil.transform.CircleCropTransformation

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