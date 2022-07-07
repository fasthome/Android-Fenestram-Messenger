package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.contacts_impl.databinding.ContactItemBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class ContactsAdapter : AsyncListDifferDelegationAdapter<ContactsViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createContactsAdapterDelegate()
    )
) {}

fun createContactsAdapterDelegate() =
    adapterDelegateViewBinding<ContactsViewItem, ContactItemBinding>(
        ContactItemBinding::inflate,
    ) {
        bindWithBinding {
            contactName.text = item.name
            newMessage.visibility = item.newMessageVisibility
        }
    }