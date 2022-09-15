package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ConversationItemGroupBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ConversationItemReceiveBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ConversationItemSelfBinding
import io.fasthome.fenestram_messenger.messenger_impl.databinding.ConversationItemSystemBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.getStatusIcon
import io.fasthome.fenestram_messenger.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConversationAdapter(onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit) :
    AsyncListDifferDelegationAdapter<ConversationViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            ConversationViewItem::id,
            ConversationViewItem::sentStatus
        ),
        AdapterUtil.adapterDelegatesManager(
            createConversationSelfAdapterDelegate(),
            createConversationReceiveAdapterDelegate(),
            createConversationGroupAdapterDelegate(onGroupProfileItemClicked),
            createConversationSystemAdapterDelegate()
        )
    )

fun createConversationSelfAdapterDelegate() =
    adapterDelegateViewBinding<ConversationViewItem.Self, ConversationItemSelfBinding>(
        ConversationItemSelfBinding::inflate
    ) {
        bindWithBinding {
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationReceiveAdapterDelegate() =
    adapterDelegateViewBinding<ConversationViewItem.Receive, ConversationItemReceiveBinding>(
        ConversationItemReceiveBinding::inflate
    ) {
        bindWithBinding {
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
        }
    }

fun createConversationGroupAdapterDelegate(onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Group, ConversationItemGroupBinding>(
        ConversationItemGroupBinding::inflate
    ) {
        binding.root.onClick {
            onGroupProfileItemClicked(item)
        }
        bindWithBinding {
            username.setPrintableText(item.userName)
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            avatar.loadCircle(url = item.avatar)
        }
    }

fun createConversationSystemAdapterDelegate() =
    adapterDelegateViewBinding<ConversationViewItem.System, ConversationItemSystemBinding>(
        ConversationItemSystemBinding::inflate
    ) {
        bindWithBinding {
            date.setPrintableText(item.content)
        }
    }