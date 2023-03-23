package io.fasthome.component.image_viewer.adapter

import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.fasthome.component.databinding.ItemImageViewerBinding
import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.dp

class ImageViewerAdapter(
    onDownSwipe: () -> Unit,
    onRootAlphaChanged: (Float) -> Unit,
    onToggleScroll: (Boolean) -> Unit,
) : PagerDelegateAdapter<ImageViewerModel>(
    AdapterUtil.diffUtilItemCallbackEquals(ImageViewerModel::imageGallery),
    delegates = listOf(
        createImageAdapterDelegate(onDownSwipe, onRootAlphaChanged, onToggleScroll)
    )
)

fun createImageAdapterDelegate(
    onDownSwipe: () -> Unit,
    onRootAlphaChanged: (Float) -> Unit,
    onToggleScroll: (Boolean) -> Unit,
) =
    createAdapterDelegate(
        inflate = ItemImageViewerBinding::inflate,
        bind = { item: ImageViewerModel, binding: ItemImageViewerBinding ->
            binding.image.setOnSwipeDownListener {
                onDownSwipe()
            }
            binding.image.setOnAlphaChangedListener { alpha ->
                onRootAlphaChanged(alpha)
            }
            binding.image.setOnScrollToggleListener { state ->
                onToggleScroll(state)
            }
            binding.image.apply {
                item.imageContent?.let {
                    setContent(it, FitCenter(), RoundedCorners(1.dp))
                }
                item.imageUrl?.let {
                    loadRounded(it, radius = 1, transform = FitCenter())
                }
                item.imageGallery?.let {
                    loadRounded(it.uri, radius = 1.dp, transform = FitCenter())
                }
            }
        }
    )
