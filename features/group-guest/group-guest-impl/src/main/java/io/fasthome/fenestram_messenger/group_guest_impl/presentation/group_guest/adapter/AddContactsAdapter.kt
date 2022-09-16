/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.AddContactItemBinding
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.InviteItemBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.model.AddContactViewItem
import io.fasthome.fenestram_messenger.util.*


class ContactsAdapter(
    onItemClicked: (AddContactViewItem.AddContact) -> Unit,
    selectActive: Boolean = true,
    onFooterClicked: (String) -> Unit
) :
    AsyncListDifferDelegationAdapter<AddContactViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createContactsAdapterDelegate(onItemClicked, selectActive),
            createFooterAdapterDelegate(onFooterClicked)
        )
    ) {}

fun createContactsAdapterDelegate(
    onItemClicked: (AddContactViewItem.AddContact) -> Unit,
    selectActive: Boolean
) =
    adapterDelegateViewBinding<AddContactViewItem.AddContact, AddContactItemBinding>(
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

fun createFooterAdapterDelegate(
    onFooterClicked: (String) -> Unit,
) =
    adapterDelegateViewBinding<AddContactViewItem.Footer, InviteItemBinding>(
        InviteItemBinding::inflate,
    ) {
        binding.root.onClick {
            onFooterClicked(item.link)
        }
        bindWithBinding {
            linkField.text = item.link
        }
    }