package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.contacts_impl.databinding.ContactItemBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.util.*

class ContactsAdapter(onItemClicked : (ContactsViewItem) -> Unit) : AsyncListDifferDelegationAdapter<ContactsViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createContactsAdapterDelegate(onItemClicked)
    )
) {}

fun createContactsAdapterDelegate(onItemClicked: (ContactsViewItem) -> Unit) =
    adapterDelegateViewBinding<ContactsViewItem, ContactItemBinding>(
        ContactItemBinding::inflate,
    ) {
        binding.root.onClick{
            onItemClicked(item)
        }
        bindWithBinding {
            contactName.setPrintableText(item.name)
            newMessage.visibility = item.newMessageVisibility
        }
    }