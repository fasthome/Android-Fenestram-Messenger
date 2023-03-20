package io.fasthome.component.file_selector

import android.net.Uri
import android.view.ViewGroup
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import io.fasthome.component.databinding.ItemImageSelectFromBinding
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.dp
import io.fasthome.fenestram_messenger.util.increaseHitArea
import io.fasthome.fenestram_messenger.util.onClick

class FileSelectorAdapter(
    onImageClick: (currentImage: FileSelectorViewItem) -> Unit,
    onCheckImage: (image: FileSelectorViewItem) -> Unit,
) : PagerDelegateAdapter<FileSelectorViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(FileSelectorViewItem::toString),
    delegates = listOf(
        createImageAdapterDelegate(
            onImageClick = onImageClick,
            onCheckImage = onCheckImage
        )
    )
)

fun createImageAdapterDelegate(
    onImageClick: (currentImage: FileSelectorViewItem) -> Unit,
    onCheckImage: (image: FileSelectorViewItem) -> Unit,
) =
    createAdapterDelegate(
        inflate = ItemImageSelectFromBinding::inflate,
        bind = { item: FileSelectorViewItem, binding: ItemImageSelectFromBinding ->
            binding.root.onClick {
                onImageClick(item)
            }
            binding.cbSelect.isChecked = item.isChecked
            if(item.isChecked) {
                binding.ivImage.startAnimation(item.getAnimation())
            }
            binding.ivImage.loadRounded(uri = item.content.uri, radius = 2.dp, sizeMultiplier = .7f, overridePair = 80.dp to 80.dp)
            binding.cbSelect.increaseHitArea(8.dp)
            binding.cbSelect.onClick {
                item.isChecked = binding.cbSelect.isChecked
                onCheckImage(item)
                binding.ivImage.startAnimation(item.getAnimation())
            }


            val layoutParams: ViewGroup.LayoutParams = binding.root.layoutParams
            if (layoutParams is FlexboxLayoutManager.LayoutParams) {
                layoutParams.flexGrow = 1.0f
                layoutParams.alignSelf = AlignItems.FLEX_START
            }
            binding.root.layoutParams = layoutParams
        }
    )