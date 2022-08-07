package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.ImageItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class ImagesAdapter : AsyncListDifferDelegationAdapter<RecentImagesViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createImagesAdapterDelegate()
    )
) {}

fun createImagesAdapterDelegate() =
    adapterDelegateViewBinding<RecentImagesViewItem, ImageItemBinding>(
        ImageItemBinding::inflate,
    ) {
        bindWithBinding {
            photoItem.setImageDrawable(getDrawable(item.image))
        }
    }