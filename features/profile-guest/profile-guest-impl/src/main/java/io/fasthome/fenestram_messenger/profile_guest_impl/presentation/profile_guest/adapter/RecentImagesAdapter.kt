package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter

import androidx.core.view.isVisible
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.setContent
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.RecentImageItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.*

class RecentImagesAdapter(
    onMoreClicked: () -> Unit,
    onItemClicked: (position : Int) -> Unit
) : AsyncListDifferDelegationAdapter<RecentImagesViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createPhotosAdapterDelegate(onMoreClicked, onItemClicked)
    )
) {}

fun createPhotosAdapterDelegate(onMoreClicked: () -> Unit, onItemClicked: (position: Int) -> Unit) =
    adapterDelegateViewBinding<RecentImagesViewItem, RecentImageItemBinding>(
        RecentImageItemBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(adapterPosition)
        }
        bindWithBinding {
            recentImageItem.setContent(item.image, CenterCrop(), RoundedCorners(12.dp))
            recentImageShowAll.isVisible = item.hasMoreImages
            recentImageShowAll.setPrintableText(item.moreImagesCount)
            if (item.hasMoreImages) {
                root.onClick {
                    onMoreClicked()
                }
            }
        }
    }