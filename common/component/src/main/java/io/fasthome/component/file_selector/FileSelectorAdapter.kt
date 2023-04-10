package io.fasthome.component.file_selector

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
            if (item.isChecked) {
                binding.ivImage.startAnimation(item.getAnimation())
            }
            binding.ivImage.loadRounded(
                uri = item.content.uri,
                radius = 2.dp,
                sizeMultiplier = .7f,
                overridePair = 110.dp to 110.dp
            )
            binding.cbSelect.increaseHitArea(8.dp)
            binding.cbSelect.onClick {
                item.isChecked = binding.cbSelect.isChecked
                onCheckImage(item)
                binding.ivImage.startAnimation(item.getAnimation())
            }
        }
    )