package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ConversationItemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding

class ConversationAdapter : AsyncListDifferDelegationAdapter<ConversationViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createConversationAdapterDelegate()
    )
) {}

fun createConversationAdapterDelegate() = adapterDelegateViewBinding<ConversationViewItem, ConversationItemBinding>(
    ConversationItemBinding::inflate){
    bindWithBinding {

        messageContent.text = item.content
        sendTimeView.text = item.time
    }
}