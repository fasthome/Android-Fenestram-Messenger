/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderAddedContactBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem
import io.fasthome.fenestram_messenger.util.*


class AddedContactsAdapter(onItemClicked: (ContactViewItem) -> Unit, onRemoveClicked: (ContactViewItem) -> Unit) :
    AsyncListDifferDelegationAdapter<ContactViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(keyExtractor = ContactViewItem::userId),
        AdapterUtil.adapterDelegatesManager(
            createAddedContactsAdapterDelegate(onItemClicked, onRemoveClicked)
        )
    ) {}

fun createAddedContactsAdapterDelegate(
    onItemClicked: (ContactViewItem) -> Unit,
    onRemoveClicked: (ContactViewItem) -> Unit
) =
    adapterDelegateViewBinding<ContactViewItem, HolderAddedContactBinding>(
        HolderAddedContactBinding::inflate,
    ) {
        binding.root.onClick {
            item.isSelected = !item.isSelected
            onItemClicked(item)
        }
        binding.remove.onClick {
            onRemoveClicked(item)
        }
        bindWithBinding {
            contactName.setPrintableText(item.userName)
            contactAvatar.loadCircle(
                url = item.avatar,
                placeholderRes = R.drawable.ic_avatar_placeholder
            )
        }
    }