package io.fasthome.fenestram_messenger.messenger_impl.presentation.file_selector

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.core.net.toFile
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.component.databinding.ItemImageSelectFromBinding
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.uikit.paging.PagerDelegateAdapter
import io.fasthome.fenestram_messenger.uikit.paging.createAdapterDelegate
import io.fasthome.fenestram_messenger.util.*

class FileSelectorAdapter(
    onImageClick: (currentImage: GalleryImage) -> Unit,
    onCheckImage: (image: GalleryImage) -> Unit,
) : PagerDelegateAdapter<GalleryImage>(
    AdapterUtil.diffUtilItemCallbackEquals(Uri::toString),
    delegates = listOf(
        createImageAdapterDelegate(
            onImageClick = onImageClick,
            onCheckImage = onCheckImage
        )
    )
)


fun createImageAdapterDelegate(
    onImageClick: (currentImage: GalleryImage) -> Unit,
    onCheckImage: (image: GalleryImage) -> Unit,
) =
    createAdapterDelegate<GalleryImage, ItemImageSelectFromBinding>(
        inflate = ItemImageSelectFromBinding::inflate,
        bind = { item: GalleryImage, binding: ItemImageSelectFromBinding ->
            binding.root.onClick {
                Log.d("SelectFromConversationAdapter", "onImageClick, pos: ${item.cursorPosition}")
                onImageClick(item)
            }
            binding.cbSelect.isChecked = item.isChecked
            binding.ivImage.loadRounded(uri = item.uri, radius = 2.dp, sizeMultiplier = .7f)
            binding.cbSelect.onClick {
                item.isChecked = binding.cbSelect.isChecked
                onCheckImage(item)
                val anim = if (binding.cbSelect.isChecked) AnimationUtil.getScaleAnimation(
                    1f,
                    .95f
                ) else AnimationUtil.getScaleAnimation(.95f, 1f)
                binding.ivImage.startAnimation(anim)
            }


            val layoutParams: ViewGroup.LayoutParams = binding.root.layoutParams
            if (layoutParams is FlexboxLayoutManager.LayoutParams) {
                layoutParams.flexGrow = 1.0f
                layoutParams.alignSelf = AlignItems.FLEX_START
            }
            binding.root.layoutParams = layoutParams
        }
    )