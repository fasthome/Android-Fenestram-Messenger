/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.core.ui.extensions

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.fasthome.fenestram_messenger.core.R
import io.fasthome.fenestram_messenger.util.dp

fun ImageView.loadRounded(
    url: String?,
    placeholderRes: Int? = null,
    radius: Int = 5.dp
) {
    var plcRes = placeholderRes
    if (plcRes == null) {
        plcRes = R.drawable.shape_placeholder_gray
    }
    Glide
        .with(this)
        .load(url)
        .transform(CenterCrop(), RoundedCorners(radius))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(plcRes)
        .into(this)
}

fun ImageView.loadRounded(
    bitmap: Bitmap?,
    placeholderRes: Int? = null,
    radius: Int = 5.dp
) {
    var plcRes = placeholderRes
    if (plcRes == null) {
        plcRes = R.drawable.shape_placeholder_gray
    }
    Glide
        .with(this)
        .load(bitmap)
        .transform(CenterCrop(), RoundedCorners(radius))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(plcRes)
        .into(this)
}

fun ImageView.loadCircle(
    url: String?,
    placeholderRes: Int? = null,
    onLoadFailed: () -> Unit = {},
    onResourceReady: () -> Unit = {},
) {
    if (url.isNullOrEmpty()) {
        placeholderRes?.let {
            Glide
                .with(this)
                .load(placeholderRes)
                .into(this)
        }
    } else {
        Glide
            .with(this)
            .load(url)
            .transform(CircleCrop())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onLoadFailed()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onResourceReady()
                    return false
                }
            })
            .into(this)
    }

}

fun ImageView.loadCircle(
    @DrawableRes imageRes: Int?,
    placeholderRes: Int = R.drawable.shape_placeholder_gray
) {
    checkNotNull(imageRes)
    Glide
        .with(this)
        .load(imageRes)
        .transform(CircleCrop())
        .placeholder(placeholderRes)
        .into(this)
}

fun ImageView.loadCircle(bitmap: Bitmap?, placeholderRes: Int = R.drawable.shape_placeholder_gray) {
    checkNotNull(bitmap)
    Glide
        .with(this)
        .load(bitmap)
        .transform(CircleCrop())
        .placeholder(placeholderRes)
        .into(this)
}