package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.RecentImageItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentImagesViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class RecentImagesAdapter : AsyncListDifferDelegationAdapter<RecentImagesViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createPhotosAdapterDelegate()
    )
) {}

fun createPhotosAdapterDelegate() =
    adapterDelegateViewBinding<RecentImagesViewItem, RecentImageItemBinding>(
        RecentImageItemBinding::inflate,
    ) {
        bindWithBinding {
            recentImageItem.setImageDrawable(getDrawable(item.image))
            recentImageShowAll.isVisible = item.showAll
            recentImageShowAll.text = "${item.imageCount}+"
        }
    }