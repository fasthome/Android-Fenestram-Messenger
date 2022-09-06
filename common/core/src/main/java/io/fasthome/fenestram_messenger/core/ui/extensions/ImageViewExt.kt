/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.core.ui.extensions

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation

fun ImageView.loadCircle(url: String?, placeholderRes: Int? = null) {
    if(url.isNullOrEmpty()) return
    this.load(url) {
        placeholderRes?.let {
            placeholder(placeholderRes)
        }
        transformations(CircleCropTransformation())
    }
}