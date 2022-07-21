package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.adapter

import android.view.View
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.MessangerChatItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class MessengerAdapter() : AsyncListDifferDelegationAdapter<MessengerViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createMessengerAdapter()
    )
) {}

fun createMessengerAdapter() = adapterDelegateViewBinding<MessengerViewItem, MessangerChatItemBinding>(
    MessangerChatItemBinding::inflate){

    bindWithBinding {
        nameView.text = item.name
    }

    }