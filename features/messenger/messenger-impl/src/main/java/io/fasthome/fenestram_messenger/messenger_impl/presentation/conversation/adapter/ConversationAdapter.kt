package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.databinding.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.core.R
import kotlin.math.roundToInt

class ConversationAdapter(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onSelfMessageClicked: (ConversationViewItem.Self) -> Unit,
    onImageClicked: (String) -> Unit,
    onDocumentClicked: (String) -> Unit
) :
    AsyncListDifferDelegationAdapter<ConversationViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            ConversationViewItem::id,
            ConversationViewItem::sentStatus
        ),
        AdapterUtil.adapterDelegatesManager(
            createConversationSelfTextAdapterDelegate(onSelfMessageClicked),
            createConversationSelfImageAdapterDelegate(onSelfMessageClicked, onImageClicked),
            createConversationSelfDocumentAdapterDelegate(onSelfMessageClicked, onDocumentClicked),
            createConversationReceiveTextAdapterDelegate(),
            createConversationReceiveImageAdapterDelegate(onImageClicked),
            createConversationReceiveDocumentAdapterDelegate(onDocumentClicked),
            createConversationGroupTextAdapterDelegate(onGroupProfileItemClicked),
            createConversationGroupImageAdapterDelegate(onGroupProfileItemClicked, onImageClicked),
            createConversationGroupDocumentAdapterDelegate(onGroupProfileItemClicked, onDocumentClicked),
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

fun createConversationSelfImageAdapterDelegate(
    onSelfMessageClicked: (ConversationViewItem.Self) -> Unit,
    onImageClicked: (String) -> Unit
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

fun createConversationSelfDocumentAdapterDelegate(
    onSelfMessageClicked: (ConversationViewItem.Self) -> Unit,
    onDocumentClicked: (String) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Document, ConversationItemSelfDocumentBinding>(
        ConversationItemSelfDocumentBinding::inflate
    ) {
        binding.messageContent.onClick {
            onDocumentClicked(item.content)
        }
        binding.root.onClick {
            onSelfMessageClicked(item)
        }
        bindWithBinding {
            fileName.text = item.file?.name
            fileSize.text = "${
                item.file?.let {
                    (it.length().toFloat() / (1024 * 1024) * 1000).roundToInt() / 1000f
                }
            }МБ"
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

fun createConversationReceiveDocumentAdapterDelegate(onDocumentClicked: (String) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Document, ConversationItemReceiveDocumentBinding>(
        ConversationItemReceiveDocumentBinding::inflate
    ) {
        binding.messageContent.onClick {
            onDocumentClicked(item.content)
        }
        bindWithBinding {
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

fun createConversationGroupDocumentAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onDocumentClicked: (String) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Document, ConversationItemGroupDocumentBinding>(
        ConversationItemGroupDocumentBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        binding.messageContent.onClick {
            onDocumentClicked(item.content)
        }
        bindWithBinding {
            username.setPrintableText(item.userName)
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