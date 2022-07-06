package io.fasthome.fenestram_messenger.contacts_impl.presentation.recycler_view

import android.view.View
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.contacts_impl.databinding.ContactItemBinding
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class ContactsAdapter : AsyncListDifferDelegationAdapter<ContactsModel>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createContactsAdapterDelegate()
    )
) {}

fun createContactsAdapterDelegate() =
    adapterDelegateViewBinding<ContactsModel, ContactItemBinding>(
        ContactItemBinding::inflate,
    ) {
        bindWithBinding {
            contactName.text = item.name
            if (item.newMessage) newMessage.visibility = View.VISIBLE else newMessage.visibility =
                View.GONE
        }
    }