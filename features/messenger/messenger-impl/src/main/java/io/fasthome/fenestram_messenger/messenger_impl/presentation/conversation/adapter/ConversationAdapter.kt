package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.databinding.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.*

class ConversationAdapter(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onSelfMessageClicked: (ConversationViewItem.Self) -> Unit
) :
    AsyncListDifferDelegationAdapter<ConversationViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            ConversationViewItem::id,
            ConversationViewItem::sentStatus
        ),
        AdapterUtil.adapterDelegatesManager(
            createConversationSelfTextAdapterDelegate(onSelfMessageClicked),
            createConversationSelfImageAdapterDelegate(onSelfMessageClicked),
            createConversationReceiveTextAdapterDelegate(),
            createConversationReceiveImageAdapterDelegate(),
            createConversationGroupTextAdapterDelegate(onGroupProfileItemClicked),
            createConversationGroupImageAdapterDelegate(onGroupProfileItemClicked),
            createConversationSystemAdapterDelegate()
        )
    )

fun createConversationSelfTextAdapterDelegate(onSelfMessageClicked: (ConversationViewItem.Self) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Text, ConversationItemSelfTextBinding>(
        ConversationItemSelfTextBinding::inflate
    ) {
        binding.root.onClick {
            onSelfMessageClicked(item)
        }
        bindWithBinding {
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationSelfImageAdapterDelegate(onSelfMessageClicked: (ConversationViewItem.Self) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Image, ConversationItemImageBinding>(
        ConversationItemImageBinding::inflate
    ) {
        binding.root.onClick {
            onSelfMessageClicked(item)
        }
        bindWithBinding {
            when {
                item.bitmap != null -> {
                    messageContent.loadRounded(item.bitmap)
                }
                else -> {
                    messageContent.loadRounded(item.content)
                }
            }
            sendTimeView.setPrintableText(item.time)
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationReceiveTextAdapterDelegate() =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Text, ConversationItemReceiveTextBinding>(
        ConversationItemReceiveTextBinding::inflate
    ) {
        bindWithBinding {
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
        }
    }

fun createConversationReceiveImageAdapterDelegate() =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Image, ConversationItemReceiveImageBinding>(
        ConversationItemReceiveImageBinding::inflate
    ) {
        bindWithBinding {
            messageContent.loadRounded(item.content)
            sendTimeView.setPrintableText(item.time)
        }
    }

fun createConversationGroupTextAdapterDelegate(onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Text, ConversationItemGroupTextBinding>(
        ConversationItemGroupTextBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        bindWithBinding {
            username.setPrintableText(item.userName)
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            avatar.loadCircle(url = item.avatar)
        }
    }

fun createConversationGroupImageAdapterDelegate(onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Image, ConversationItemGroupImageBinding>(
        ConversationItemGroupImageBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        bindWithBinding {
            username.setPrintableText(item.userName)
            messageContent.loadRounded(item.content)
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