/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.adapter

import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.HolderContactBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem
import io.fasthome.fenestram_messenger.util.*


class ContactsAdapter(
    textColor: Int,
    onItemClicked: (ContactViewItem) -> Unit,
    selectActive: Boolean = true,
) :
    AsyncListDifferDelegationAdapter<ContactViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createContactsAdapterDelegate(textColor, onItemClicked, selectActive)
        )
    ) {}

fun createContactsAdapterDelegate(textColor: Int,onItemClicked: (ContactViewItem) -> Unit, selectActive: Boolean) =
    adapterDelegateViewBinding<ContactViewItem, HolderContactBinding>(
        HolderContactBinding::inflate,
    ) {
        binding.root.onClick {
            onItemClicked(item)
        }
        bindWithBinding {
            contactName.setTextColor(textColor)
            contactName.setPrintableText(item.userName)
            contactAvatar.loadCircle(
                url = item.avatar,
                placeholderRes = R.drawable.ic_avatar_placeholder
            )
            if (selectActive) {
                root.setBackgroundResource(item.backgroundRes)
                check.isVisible = item.isSelected
            }
        }
    }