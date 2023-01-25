package io.fasthome.component.imageViewer

import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.component.databinding.ItemImageViewerBinding
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.dp

class ImageViewerAdapter(
    onDownSwipe: () -> Unit,
    onRootAlphaChanged: (Float) -> Unit,
    onToggleScroll: (Boolean) -> Unit,
) : AsyncListDifferDelegationAdapter<ImageViewerModel>(
    AdapterUtil.diffUtilItemCallbackEquals(
        ImageViewerModel::imageUrl
    ), AdapterUtil.adapterDelegatesManager(
        createImageAdapterDelegate(onDownSwipe, onRootAlphaChanged, onToggleScroll)
    )
)

fun createImageAdapterDelegate(
    onDownSwipe: () -> Unit,
    onRootAlphaChanged: (Float) -> Unit,
    onToggleScroll: (Boolean) -> Unit,
) =
    adapterDelegateViewBinding<ImageViewerModel, ItemImageViewerBinding>(
        ItemImageViewerBinding::inflate
    ) {
        binding.image.setOnSwipeDownListener {
            onDownSwipe()
        }
        binding.image.setOnAlphaChangedListener { alpha ->
            onRootAlphaChanged(alpha)
        }
        binding.image.setOnScrollToggleListener { state ->
            onToggleScroll(state)
        }
        bindWithBinding {
            binding.image.apply {
                item.imageContent?.let {
                    setContent(it, FitCenter(), RoundedCorners(1.dp))
                }
                item.imageUrl?.let {
                    loadRounded(it, radius = 1, transform = FitCenter())
                }
            }
        }
    }