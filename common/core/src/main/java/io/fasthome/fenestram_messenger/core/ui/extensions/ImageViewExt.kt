/**
 * Created by Dmitry Popov on 25.08.2022.
 */
package io.fasthome.fenestram_messenger.core.ui.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.RequestBuilder
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
import io.fasthome.fenestram_messenger.uikit.custom_view.emptyAvatarWithUsername
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.ContentLoaderFactory
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.UrlLoadableContent
import io.fasthome.fenestram_messenger.util.dp
import org.koin.core.component.KoinComponent
import java.nio.ByteBuffer


@GlideModule
class AppGlideModule : AppGlideModule(), KoinComponent {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(
            Content.LoadableContent::class.java,
            ByteBuffer::class.java,
            ContentLoaderFactory()
        )
    }
}

fun ImageView.loadRounded(
    url: String?,
    placeholderRes: Int? = null,
    radius: Int = 5.dp,
    transform: BitmapTransformation? = CenterCrop(),
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
    uri: Uri?,
    placeholderRes: Int? = null,
    radius: Int = 5.dp,
    transform: BitmapTransformation? = CenterCrop(),
    progressBar: ProgressBar? = null,
    sizeMultiplier: Float = 1f,
    overridePair: Pair<Int, Int>? = null
) {
    progressBar?.isVisible = true
    var plcRes = placeholderRes
    if (plcRes == null) {
        plcRes = R.drawable.shape_placeholder_gray
    }

    Glide
        .with(this)
        .load(uri)
        .sizeMultiplier(sizeMultiplier)
        .transform(transform, RoundedCorners(radius))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean,
            ): Boolean {
                progressBar?.isVisible = false
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                progressBar?.isVisible = false
                return false
            }

        })
        .placeholder(plcRes)
        .override(overridePair)
        .into(this)
}

fun ImageView.loadRounded(
    bitmap: Bitmap?,
    placeholderRes: Int? = null,
    radius: Int = 5.dp,
    transform: BitmapTransformation? = CenterCrop(),
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

fun ImageView.loadAvatarWithGradient(
    url: String?,
    username: String? = null,
    onLoadFailed: () -> Unit = {},
    onResourceReady: () -> Unit = {},
    progressBar: ProgressBar? = null
) {

    if (url.isNullOrEmpty()) {
        progressBar?.alpha = 0f
        Glide
            .with(this)
            .load(emptyAvatarWithUsername(username))
            .into(this)
    } else {
        progressBar?.alpha = 1f
        Glide
            .with(this)
            .load(url)
            .placeholder(emptyAvatarWithUsername(username)?.toDrawable(context.resources))
            .transform(CircleCrop())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar?.alpha = 0f
                    onLoadFailed()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar?.alpha = 0f
                    onResourceReady()
                    return false
                }
            })
            .into(this)
    }
}

fun ImageView.loadCircle(
    url: String?,
    placeholderRes: Int? = null,
    onLoadFailed: () -> Unit = {},
    onResourceReady: () -> Unit = {},
    progressBar: ProgressBar? = null
) {
    if (url.isNullOrEmpty()) {
        placeholderRes?.let {
            Glide
                .with(this)
                .load(placeholderRes)
                .into(this)
        }
    } else {
        progressBar?.alpha = 1f
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
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar?.alpha = 0f
                    onLoadFailed()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar?.alpha = 0f
                    onResourceReady()
                    return false
                }
            })
            .into(this)
    }

}

fun ImageView.loadCircle(
    @DrawableRes imageRes: Int?,
    placeholderRes: Int = R.drawable.shape_placeholder_gray,
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
                    when (content) {
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

fun loadBitmap(
    context: Context,
    url: String,
    placeholderRes: Int = R.drawable.ic_avatar_placeholder
): Bitmap {
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

fun loadOriginalBitmap(context: Context, url: String): Bitmap {
    return try {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .submit()
            .get()
    } catch (e: Exception) {
        throw e
    }
}

fun loadOriginalGif(context: Context, url: String): ByteBuffer {
    return try {
        Glide.with(context)
            .asGif()
            .load(url)
            .submit()
            .get()
            .buffer
    } catch (e: Exception) {
        throw e
    }
}

fun RequestBuilder<Drawable>.override(overridePair: Pair<Int, Int>?): RequestBuilder<Drawable> {
    if (overridePair != null) {
        return this.clone().override(overridePair.first, overridePair.second)
    }
    return this
}