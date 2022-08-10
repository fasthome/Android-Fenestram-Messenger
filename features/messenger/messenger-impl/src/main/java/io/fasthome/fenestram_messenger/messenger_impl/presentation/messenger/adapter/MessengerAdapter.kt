package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MessangerChatItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.setPrintableText

class MessengerAdapter(OnClickChat:(Long) -> Unit) : AsyncListDifferDelegationAdapter<MessengerViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createMessengerAdapter(OnClickChat)
    )
) {}

fun createMessengerAdapter(chatClicked:(Long) -> Unit) = adapterDelegateViewBinding<MessengerViewItem, MessangerChatItemBinding>(
    MessangerChatItemBinding::inflate){

    binding.root.setOnClickListener{
        chatClicked(item.id)
    }
    bindWithBinding {
        nameView.text = item.name
        lastMessage.setPrintableText(item.lastMessage)

    }

    }

