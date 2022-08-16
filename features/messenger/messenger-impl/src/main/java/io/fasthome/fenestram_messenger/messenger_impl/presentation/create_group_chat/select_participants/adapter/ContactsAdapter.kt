/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderContactBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem
import io.fasthome.fenestram_messenger.util.*


class ContactsAdapter(onItemClicked: (ContactViewItem) -> Unit, selectActive: Boolean = true) :
    AsyncListDifferDelegationAdapter<ContactViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createContactsAdapterDelegate(onItemClicked, selectActive)
        )
    ) {}

fun createContactsAdapterDelegate(onItemClicked: (ContactViewItem) -> Unit, selectActive: Boolean) =
    adapterDelegateViewBinding<ContactViewItem, HolderContactBinding>(
        HolderContactBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            contactName.setPrintableText(item.userName)
            if (selectActive) {
                root.setBackgroundResource(item.backgroundRes)
                check.isVisible = item.isSelected
            }
        }
    }