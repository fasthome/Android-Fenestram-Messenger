/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.core.ui.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.fasthome.fenestram_messenger.core.R
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.ContentLoaderFactory
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UrlLoadableContent
import io.fasthome.fenestram_messenger.util.dp
import org.koin.core.component.KoinComponent
import java.nio.ByteBuffer

@GlideModule
class AppGlideModule : AppGlideModule(), KoinComponent {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(Content.LoadableContent::class.java, ByteBuffer::class.java, ContentLoaderFactory())
    }
}

fun ImageView.loadRounded(
    url: String?,
    placeholderRes: Int? = null,
    radius: Int = 5.dp,
    transform: BitmapTransformation? = CenterCrop()
) {
    var plcRes = placeholderRes
    if (plcRes == null) {
        plcRes = R.drawable.shape_placeholder_gray
    }
    Glide
        .with(this)
        .load(url)
        .transform(transform, RoundedCorners(radius))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(plcRes)
        .into(this)
}

fun ImageView.loadRounded(
    bitmap: Bitmap?,
    placeholderRes: Int? = null,
    radius: Int = 5.dp,
    transform: BitmapTransformation? = CenterCrop()
) {
    var plcRes = placeholderRes
    if (plcRes == null) {
        plcRes = R.drawable.shape_placeholder_gray
    }
    Glide
        .with(this)
        .load(bitmap)
        .transform(transform, RoundedCorners(radius))
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

fun ImageView.setContent(content: Content, vararg transformations: Transformation<Bitmap>) {
    GlideApp
        .with(this)
        .load(
            when (content) {
                is Content.FileContent -> content.file
                is Content.LoadableContent -> {
                    when(content){
                        is UrlLoadableContent -> {
                            content.url
                        }
                        else -> {
                            content
                        }
                    }
                }
            }
        )
        .transform(*transformations)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .skipMemoryCache(true)
        .into(this)
}

fun loadBitmap(context: Context, url: String, placeholderRes: Int = R.drawable.ic_avatar_placeholder): Bitmap {
    return try {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .error(placeholderRes)
            .transform(CircleCrop())
            .submit(100, 100)
            .get()
    } catch (e: Exception) {
        Glide.with(context)
            .asBitmap()
            .load(placeholderRes)
            .submit(100, 100)
            .get()
    }
}