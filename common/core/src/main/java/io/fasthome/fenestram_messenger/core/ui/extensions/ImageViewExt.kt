/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.core.ui.extensions

import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.CircleCropTransformation
import io.fasthome.fenestram_messenger.core.R

fun ImageView.loadCircle(url: String?, placeholderRes: Int? = null) {
    if (url.isNullOrEmpty())
        placeholderRes?.let { this.load(placeholderRes) }
    else
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