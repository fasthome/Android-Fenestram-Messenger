package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderImageBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.MetaInfo
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.onClick

class ConversationImageAdapter(
    onImageClick: (meta: MetaInfo) -> Unit,
) : AsyncListDifferDelegationAdapter<MetaInfo>(
    AdapterUtil.diffUtilItemCallbackEquals(
        MetaInfo::url
    ), AdapterUtil.adapterDelegatesManager(
        createImageAdapterDelegate(onImageClick = onImageClick)
    )
)


fun createImageAdapterDelegate(onImageClick: (meta: MetaInfo) -> Unit) =
    adapterDelegateViewBinding<MetaInfo, HolderImageBinding>(
        HolderImageBinding::inflate
    ) {
        binding.root.onClick {
            onImageClick(item)
        }
        bindWithBinding {
            ivImage.loadRounded(item.url)
        }
    }