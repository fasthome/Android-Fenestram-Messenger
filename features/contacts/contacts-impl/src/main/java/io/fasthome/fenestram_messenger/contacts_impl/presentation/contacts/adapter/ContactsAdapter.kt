package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.databinding.ContactItemBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.util.*

class ContactsAdapter(
    textColor: Int?,
    onItemClicked: (ContactsViewItem) -> Unit,
) :
    AsyncListDifferDelegationAdapter<ContactsViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createContactsAdapterDelegate(textColor, onItemClicked),
        )
    )

fun createContactsAdapterDelegate(
    textColor: Int?,
    onItemClicked: (ContactsViewItem) -> Unit) =
    adapterDelegateViewBinding<ContactsViewItem, ContactItemBinding>(
        ContactItemBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            textColor?.let { it1 -> contactName.setTextColor(it1) }
            contactName.setPrintableText(item.name)
            newMessage.isVisible = false
            contactAvatar.loadCircle(
                url = item.avatar,
                placeholderRes = R.drawable.ic_avatar_placeholder
            )
        }
    }