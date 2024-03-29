/**
 * Created by Dmitry Popov on 16.09.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderAttachedDocumentBinding
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderAttachedImageBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.AttachedFile
import io.fasthome.fenestram_messenger.util.*

class AttachedAdapter(onRemoveClicked: (AttachedFile) -> Unit) :
    AsyncListDifferDelegationAdapter<AttachedFile>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createImageAdapterDelegate(onRemoveClicked),
            createDocumentAdapterDelegate(onRemoveClicked)
        )
    )

fun createImageAdapterDelegate(onRemoveClicked: (AttachedFile) -> Unit) =
    adapterDelegateViewBinding<AttachedFile.Image, HolderAttachedImageBinding>(
        HolderAttachedImageBinding::inflate
    ) {
        binding.close.increaseHitArea(16.dp)
        binding.close.onClick {
            onRemoveClicked(item)
        }
        bindWithBinding {
            image.clipToOutline = true
            image.outlineProvider = RoundedCornersOutlineProvider(5.dp.toFloat())
            image.setContent(item.content)
        }
    }

fun createDocumentAdapterDelegate(onRemoveClicked: (AttachedFile) -> Unit) =
    adapterDelegateViewBinding<AttachedFile.Document, HolderAttachedDocumentBinding>(
        HolderAttachedDocumentBinding::inflate
    ) {
        binding.close.increaseHitArea(16.dp)
        binding.close.onClick {
            onRemoveClicked(item)
        }
        bindWithBinding {
            fileName.text = item.file.name
        }
    }
