package io.fasthome.component.image_viewer.adapter

import android.view.View
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import io.fasthome.component.databinding.ItemImagePickerBinding
import io.fasthome.component.image_viewer.model.ImageViewerModel
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.dp
import io.fasthome.fenestram_messenger.util.onClick

class ImageSliderAdapter(
    onItemClicked: (view: View) -> Unit,
) : PagerDelegateAdapter<ImageViewerModel>(
    AdapterUtil.diffUtilItemCallbackEquals(ImageViewerModel::imageGallery),
    delegates = listOf(
        createImageAdapterDelegate(onItemClicked)
    )
)

fun createImageAdapterDelegate(
    onItemClicked: (view: View) -> Unit
) =
    createAdapterDelegate(
        inflate = ItemImagePickerBinding::inflate,
        bind = { item: ImageViewerModel, binding: ItemImagePickerBinding ->
            binding.root.onClick {
                onItemClicked(binding.root)
            }
            binding.ivImage.apply {
                item.imageContent?.let {
                    setContent(it, FitCenter(), RoundedCorners(1.dp))
                }
                item.imageUrl?.let {
                    loadRounded(it, radius = 5.dp)
                }
                item.imageGallery?.let {
                    loadRounded(it.uri, radius = 5.dp)
                }
            }
        }
    )
