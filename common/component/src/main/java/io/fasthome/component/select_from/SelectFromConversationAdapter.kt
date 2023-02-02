package io.fasthome.component.select_from

import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.component.databinding.ItemImageSelectFromBinding
import io.fasthome.component.gallery.GalleryImage
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.util.*

class SelectFromConversationAdapter(
    onImageClick: (currentImage: GalleryImage) -> Unit,
    onCheckImage: (image: GalleryImage) -> Unit,
) : AsyncListDifferDelegationAdapter<GalleryImage>(
    AdapterUtil.diffUtilItemCallbackEquals(
        Uri::toString
    ), AdapterUtil.adapterDelegatesManager(
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
    adapterDelegateViewBinding<GalleryImage, ItemImageSelectFromBinding>(
        ItemImageSelectFromBinding::inflate
    ) {
        binding.root.onClick {
            Log.d("SelectFromConversationAdapter", "onImageClick, pos: ${item.cursorPosition}")
            onImageClick(item)
        }
        bindWithBinding {
            cbSelect.isChecked = item.isChecked
            ivImage.loadRounded(uri = item.uri, radius = 2.dp, sizeMultiplier = .7f)
            cbSelect.onClick {
                item.isChecked = cbSelect.isChecked
                onCheckImage(item)
                val anim = if (cbSelect.isChecked) AnimationUtil.getScaleAnimation(1f,
                    .95f) else AnimationUtil.getScaleAnimation(.95f, 1f)
                ivImage.startAnimation(anim)
            }


            val layoutParams: ViewGroup.LayoutParams = root.layoutParams
            if (layoutParams is FlexboxLayoutManager.LayoutParams) {
                layoutParams.flexGrow = 1.0f
                layoutParams.alignSelf = AlignItems.FLEX_START
            }
            root.layoutParams = layoutParams
        }
    }