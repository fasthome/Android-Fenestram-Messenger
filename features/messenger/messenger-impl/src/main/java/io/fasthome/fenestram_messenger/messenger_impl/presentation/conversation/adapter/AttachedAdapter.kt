/**
 * Created by Dmitry Popov on 16.09.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ConversationItemSelfBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderAttachedImageBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFile
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.*

class AttachedAdapter() :
    AsyncListDifferDelegationAdapter<AttachedFile>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createImageAdapterDelegate()
        )
    )

fun createImageAdapterDelegate() =
    adapterDelegateViewBinding<AttachedFile.Image, HolderAttachedImageBinding>(
        HolderAttachedImageBinding::inflate
    ) {
        bindWithBinding {
            image.clipToOutline = true
            image.outlineProvider = RoundedCornersOutlineProvider(5.dp.toFloat())
            image.setImageBitmap(item.bitmap)
        }
    }
