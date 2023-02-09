package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.ImageItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.onClick

class ImagesAdapter(onItemClicked : (position : Int) -> Unit) : AsyncListDifferDelegationAdapter<RecentImagesViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createImagesAdapterDelegate(onItemClicked)
    )
) {}

fun createImagesAdapterDelegate(onItemClicked: (position: Int) -> Unit) =
    adapterDelegateViewBinding<RecentImagesViewItem, ImageItemBinding>(
        ImageItemBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(adapterPosition)
        }
        bindWithBinding {
            photoItem.setContent(item.image)
        }
    }