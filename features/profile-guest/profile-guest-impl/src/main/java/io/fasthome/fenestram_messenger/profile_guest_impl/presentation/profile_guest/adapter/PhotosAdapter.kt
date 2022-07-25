package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FileItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.PhotoItemBinding
import io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest.model.PhotosViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class PhotosAdapter : AsyncListDifferDelegationAdapter<PhotosViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createPhotosAdapterDelegate()
    )
) {}

fun createPhotosAdapterDelegate() =
    adapterDelegateViewBinding<PhotosViewItem, PhotoItemBinding>(
        PhotoItemBinding::inflate,
    ) {
        bindWithBinding {
            photoItem.setImageDrawable(getDrawable(item.image))
        }
    }