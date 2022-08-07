package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.AllFilesItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FileItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.FilesViewItem
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest_files.model.AllFilesViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class AllFilesAdapter : AsyncListDifferDelegationAdapter<AllFilesViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createAllFilesAdapterDelegate()
    )
) {}

fun createAllFilesAdapterDelegate() =
    adapterDelegateViewBinding<AllFilesViewItem, AllFilesItemBinding>(
        AllFilesItemBinding::inflate,
    ) {
        bindWithBinding {
            allFilesFileName.text = item.fileName
            allFilesFileSize.text = item.fileSize.toString()
            allFilesFileDate.text = item.date
        }
    }