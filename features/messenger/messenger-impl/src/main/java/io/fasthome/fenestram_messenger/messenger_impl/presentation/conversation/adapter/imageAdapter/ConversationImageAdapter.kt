package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.imageAdapter

import android.view.ViewGroup
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexboxLayoutManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderImageBinding
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.model.MetaInfo


class ConversationImageAdapter(
    onImageClick: (meta: MetaInfo) -> Unit,
) : AsyncListDifferDelegationAdapter<ImageAdapterItem>(
    AdapterUtil.diffUtilItemCallbackEquals(
        MetaInfo::url
    ), AdapterUtil.adapterDelegatesManager(
        createImageAdapterDelegate(onImageClick = onImageClick)
    )
)

fun createImageAdapterDelegate(onImageClick: (meta: MetaInfo) -> Unit) =
    adapterDelegateViewBinding<ImageAdapterItem, HolderImageBinding>(
        HolderImageBinding::inflate
    ) {
        binding.root.onClick {
            onImageClick(item.metaInfo)
        }
        bindWithBinding {
            ivImage.loadRounded(item.metaInfo.url, radius = 8.dp)
            if (item.size.dimenResSize != null) {
                val minSize = context.resources.getDimension(item.size.dimenResSize!!).toInt()
                ivImage.minimumWidth = minSize
                ivImage.minimumHeight = minSize
            }
            val layoutParams: ViewGroup.LayoutParams = root.layoutParams
            if (layoutParams is FlexboxLayoutManager.LayoutParams) {
                layoutParams.flexGrow = 1.0f
                layoutParams.alignSelf = AlignItems.FLEX_START
            }
            root.layoutParams = layoutParams
        }
    }