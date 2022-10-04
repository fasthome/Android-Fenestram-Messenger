package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.contacts_impl.databinding.ContactItemBinding
import io.fasthome.fenestram_messenger.contacts_impl.databinding.ContactItemHeaderBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.util.*

class ContactsAdapter(
    onItemClicked: (ContactsViewItem) -> Unit,
    onAvatarClicked: (Long) -> Unit
) :
    AsyncListDifferDelegationAdapter<ContactsViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createApiContactsAdapterDelegate(onItemClicked, onAvatarClicked),
            createLocalContactsAdapterDelegate(onItemClicked),
            createHeaderAdapterDelegate()
        )
    ) {}

fun createApiContactsAdapterDelegate(
    onItemClicked: (ContactsViewItem) -> Unit,
    onAvatarClicked: (Long) -> Unit
) =
    adapterDelegateViewBinding<ContactsViewItem.Api, ContactItemBinding>(
        ContactItemBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            contactName.setPrintableText(item.name)
            newMessage.isVisible = false
            contactAvatar.loadCircle(
                url = item.avatar
            )
            contactAvatar.setOnClickListener { onAvatarClicked(item.userId) }
        }
    }

fun createLocalContactsAdapterDelegate(onItemClicked: (ContactsViewItem) -> Unit) =
    adapterDelegateViewBinding<ContactsViewItem.Local, ContactItemBinding>(
        ContactItemBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            contactName.setPrintableText(item.name)
            newMessage.isVisible = false
        }
    }

fun createHeaderAdapterDelegate() =
    adapterDelegateViewBinding<ContactsViewItem.Header, ContactItemHeaderBinding>(
        ContactItemHeaderBinding::inflate,
    ) {
        bindWithBinding {
            contactName.setPrintableText(item.name)
        }
    }