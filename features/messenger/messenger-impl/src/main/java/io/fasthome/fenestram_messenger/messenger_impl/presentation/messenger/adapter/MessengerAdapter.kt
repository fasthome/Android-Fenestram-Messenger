package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MessangerChatItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.util.*

class MessengerAdapter(onChatClicked: (Long) -> Unit, onProfileClicked: (MessengerViewItem) -> Unit) :
    AsyncListDifferDelegationAdapter<MessengerViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(),
        AdapterUtil.adapterDelegatesManager(
            createMessengerAdapter(onChatClicked, onProfileClicked)
        )
    ) {}

fun createMessengerAdapter(chatClicked: (Long) -> Unit, onProfileClicked: (MessengerViewItem) -> Unit) =
    adapterDelegateViewBinding<MessengerViewItem, MessangerChatItemBinding>(
        MessangerChatItemBinding::inflate
    ) {

        binding.root.onClick {
            chatClicked(item.id)
        }
        binding.profilePicture.onClick {
            onProfileClicked(item)
        }
        bindWithBinding {
            nameView.setPrintableText(item.name)
            lastMessage.setPrintableText(item.lastMessage)
            timeView.setPrintableText(item.time)
            profilePicture.loadCircle(
                url = item.profileImageUrl,
                placeholderRes = R.drawable.ic_baseline_account_circle_24
            )
        }

    }

