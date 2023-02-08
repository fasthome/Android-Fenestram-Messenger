package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.RecentFileItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.RecentFilesViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class RecentFilesAdapter : AsyncListDifferDelegationAdapter<RecentFilesViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createFilesAdapterDelegate()
    )
) {}

fun createFilesAdapterDelegate() =
    adapterDelegateViewBinding<RecentFilesViewItem, RecentFileItemBinding>(
        RecentFileItemBinding::inflate,
    ) {
        bindWithBinding {
            recentFileName.text = item.path
        }
    }