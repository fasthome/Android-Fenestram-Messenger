package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ConversationItemReceiveBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ConversationItemSelfBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.AdapterUtil
import io.fasthome.fenestram_messenger.util.adapterDelegateViewBinding
import io.fasthome.fenestram_messenger.util.bindWithBinding
import io.fasthome.fenestram_messenger.util.setPrintableText

class ConversationAdapter : AsyncListDifferDelegationAdapter<ConversationViewItem>(
    AdapterUtil.diffUtilItemCallbackEquals(),
    AdapterUtil.adapterDelegatesManager(
        createConversationSelfAdapterDelegate(),
        createConversationReceiveAdapterDelegate()
    )
) {}

fun createConversationSelfAdapterDelegate() = adapterDelegateViewBinding<ConversationViewItem.Self, ConversationItemSelfBinding>(
    ConversationItemSelfBinding::inflate){
    bindWithBinding {
        messageContent.setPrintableText(item.content)
        sendTimeView.setPrintableText(item.time)
    }
}

fun createConversationReceiveAdapterDelegate() = adapterDelegateViewBinding<ConversationViewItem.Receive, ConversationItemReceiveBinding>(
    ConversationItemReceiveBinding::inflate){
    bindWithBinding {
        messageContent.setPrintableText(item.content)
        sendTimeView.setPrintableText(item.time)
    }
}