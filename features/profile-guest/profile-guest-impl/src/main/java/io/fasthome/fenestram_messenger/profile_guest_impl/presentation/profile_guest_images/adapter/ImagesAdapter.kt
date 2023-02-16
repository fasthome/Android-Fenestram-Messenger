package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_images.adapter

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.ImageItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.*

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
            photoItem.loadRounded(item.image.url, placeholderRes = R.color.gray, radius = 1.dp, CenterCrop())
        }
    }