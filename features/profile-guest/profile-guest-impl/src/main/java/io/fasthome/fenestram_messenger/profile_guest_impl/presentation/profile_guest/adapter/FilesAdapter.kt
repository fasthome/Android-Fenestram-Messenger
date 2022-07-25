package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FileItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.FilesViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class FilesAdapter : AsyncListDifferDelegationAdapter<FilesViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createFilesAdapterDelegate()
    )
) {}

fun createFilesAdapterDelegate() =
    adapterDelegateViewBinding<FilesViewItem, FileItemBinding>(
        FileItemBinding::inflate,
    ) {
        bindWithBinding {
            fileName.text = item.fileName
        }
    }