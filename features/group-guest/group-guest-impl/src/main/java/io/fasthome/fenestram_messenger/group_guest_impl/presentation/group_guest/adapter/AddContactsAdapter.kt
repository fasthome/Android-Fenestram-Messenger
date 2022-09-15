/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.AddContactItemBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.model.AddContactViewItem
import io.fasthome.fenestram_messenger.util.*


class ContactsAdapter(onItemClicked: (AddContactViewItem) -> Unit, selectActive: Boolean = true) :
    AsyncListDifferDelegationAdapter<AddContactViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createContactsAdapterDelegate(onItemClicked, selectActive)
        )
    ) {}

fun createContactsAdapterDelegate(onItemClicked: (AddContactViewItem) -> Unit, selectActive: Boolean) =
    adapterDelegateViewBinding<AddContactViewItem, AddContactItemBinding>(
        AddContactItemBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            contactName.setPrintableText(item.userName)
            if (selectActive) {
                root.setBackgroundResource(item.backgroundRes)
                checking.isVisible = item.isSelected
            }
        }
    }