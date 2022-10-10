package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import android.view.View
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.R
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.databinding.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.*

class ConversationAdapter(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onSelfMessageClicked: (ConversationViewItem.Self) -> Unit,
    onImageClicked : (String) -> Unit,
    onSelfMessageLongClicked: (ConversationViewItem.Self.Text) -> Unit,
    onReceiveMessageLongClicked: (ConversationViewItem.Receive.Text) -> Unit,
    onGroupMessageLongClicked: (ConversationViewItem.Group.Text) -> Unit,
    onSelfImageLongClicked: (ConversationViewItem.Self.Image) -> Unit,
) :
    AsyncListDifferDelegationAdapter<ConversationViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            ConversationViewItem::id,
            ConversationViewItem::sentStatus
        ),
        AdapterUtil.adapterDelegatesManager(
            createConversationSelfTextAdapterDelegate(onSelfMessageClicked, onSelfMessageLongClicked),
            createConversationSelfImageAdapterDelegate(onSelfMessageClicked, onImageClicked, onSelfImageLongClicked),
            createConversationReceiveTextAdapterDelegate(onReceiveMessageLongClicked),
            createConversationReceiveImageAdapterDelegate(onImageClicked),
            createConversationGroupTextAdapterDelegate(onGroupProfileItemClicked, onGroupMessageLongClicked),
            createConversationGroupImageAdapterDelegate(onGroupProfileItemClicked, onImageClicked),
            createConversationSystemAdapterDelegate()
        )
    )

fun createConversationSelfTextAdapterDelegate(
    onSelfMessageClicked: (ConversationViewItem.Self) -> Unit,
    onSelfMessageLongClicked: (ConversationViewItem.Self.Text) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Text, ConversationItemSelfTextBinding>(
        ConversationItemSelfTextBinding::inflate
    ) {
        binding.root.onClick {
            onSelfMessageClicked(item)
        }
        binding.root.setOnLongClickListener {
            onSelfMessageLongClicked(item)
            true
        }
        bindWithBinding {
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationSelfImageAdapterDelegate(
    onSelfMessageClicked: (ConversationViewItem.Self) -> Unit,
    onImageClicked: (String) -> Unit,
    onSelfImageLongClicked: (ConversationViewItem.Self.Image) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Image, ConversationItemImageBinding>(
        ConversationItemImageBinding::inflate
    ) {
        binding.messageContent.onClick {
            onImageClicked(item.content)
        }
        binding.root.onClick {
            onSelfMessageClicked(item)
        }
        val imageLongClick = View.OnLongClickListener {
            onSelfImageLongClicked(item)
            true
        }
        binding.root.setOnLongClickListener(imageLongClick)
        binding.messageContent.setOnLongClickListener(imageLongClick)
        bindWithBinding {
            when {
//                item.bitmap != null -> {
//                    messageContent.loadRounded(item.bitmap)
//                }
                else -> {
                    messageContent.loadRounded(item.content)
                }
            }
            sendTimeView.setPrintableText(item.time)
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationReceiveTextAdapterDelegate(onReceiveMessageLongClicked: (ConversationViewItem.Receive.Text) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Text, ConversationItemReceiveTextBinding>(
        ConversationItemReceiveTextBinding::inflate,

    ) {
        bindWithBinding {
            root.setOnLongClickListener {
                onReceiveMessageLongClicked(item)
                true
            }
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
        }
    }

fun createConversationReceiveImageAdapterDelegate(onImageClicked: (String) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Image, ConversationItemReceiveImageBinding>(
        ConversationItemReceiveImageBinding::inflate
    ) {
        binding.messageContent.onClick {
            onImageClicked(item.content)
        }
        bindWithBinding {
            messageContent.loadRounded(item.content)
            sendTimeView.setPrintableText(item.time)
        }
    }

fun createConversationGroupTextAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onGroupMessageLongClicked: (ConversationViewItem.Group.Text) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Text, ConversationItemGroupTextBinding>(
        ConversationItemGroupTextBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        binding.root.setOnLongClickListener {
            onGroupMessageLongClicked(item)
            true
        }
        bindWithBinding {
            username.setPrintableText(item.userName)
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.common_avatar)
        }
    }

fun createConversationGroupImageAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onImageClicked: (String) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Image, ConversationItemGroupImageBinding>(
        ConversationItemGroupImageBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        binding.messageContent.onClick {
            onImageClicked(item.content)
        }
        bindWithBinding {
            username.setPrintableText(item.userName)
            messageContent.loadRounded(item.content)
            sendTimeView.setPrintableText(item.time)
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.common_avatar)
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