package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import android.view.View
import androidx.core.view.isVisible
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
    onImageClicked: (String) -> Unit,
    onSelfMessageLongClicked: (ConversationViewItem.Self.Text) -> Unit,
    onReceiveMessageLongClicked: (ConversationViewItem.Receive.Text) -> Unit,
    onGroupMessageLongClicked: (ConversationViewItem.Group.Text) -> Unit,
    onSelfImageLongClicked: (ConversationViewItem.Self.Image) -> Unit,
    onDocumentClicked: (String, String?, isDownloaded: (String?) -> Unit) -> Unit
) :
    AsyncListDifferDelegationAdapter<ConversationViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            ConversationViewItem::id,
            ConversationViewItem::sentStatus,
            ConversationViewItem::timeVisible,
        ),
        AdapterUtil.adapterDelegatesManager(
            createConversationSelfTextAdapterDelegate(onSelfMessageClicked, onSelfMessageLongClicked),
            createConversationSelfImageAdapterDelegate(onSelfMessageClicked, onImageClicked, onSelfImageLongClicked),
            createConversationSelfDocumentAdapterDelegate(onSelfMessageClicked, onDocumentClicked),
            createConversationReceiveTextAdapterDelegate(onReceiveMessageLongClicked),
            createConversationReceiveImageAdapterDelegate(onImageClicked),
            createConversationReceiveDocumentAdapterDelegate(onDocumentClicked),
            createConversationGroupTextAdapterDelegate(onGroupProfileItemClicked, onGroupMessageLongClicked),
            createConversationGroupImageAdapterDelegate(onGroupProfileItemClicked, onImageClicked),
            createConversationGroupDocumentAdapterDelegate(
                onGroupProfileItemClicked,
                onDocumentClicked
            ),
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
            tvEdited.isVisible = item.isEdited
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
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
            messageContent.loadRounded(item.content)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            status.setImageResource(item.statusIcon)
            status.isVisible = item.timeVisible
        }
    }

fun createConversationSelfDocumentAdapterDelegate(
    onSelfMessageClicked: (ConversationViewItem.Self) -> Unit,
    onDocumentClicked: (String, String?, isDownloaded: (String?) -> Unit) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Document, ConversationItemSelfDocumentBinding>(
        ConversationItemSelfDocumentBinding::inflate
    ) {
        binding.messageContent.onClick {
            onDocumentClicked(item.content, item.path) {
                item.path = it
            }
        }
        binding.root.onClick {
            onSelfMessageClicked(item)
        }
        bindWithBinding {
            fileName.text = item.content.substring(item.content.lastIndexOf(".") + 1).toUpperCase()
            sendTimeView.setPrintableText(item.time)
            status.setImageResource(item.statusIcon)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
        }
    }

fun createConversationReceiveTextAdapterDelegate(onReceiveMessageLongClicked: (ConversationViewItem.Receive.Text) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Text, ConversationItemReceiveTextBinding>(
        ConversationItemReceiveTextBinding::inflate,

        ) {
        bindWithBinding {
            tvEdited.isVisible = item.isEdited
            root.setOnLongClickListener {
                onReceiveMessageLongClicked(item)
                true
            }
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
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
            sendTimeView.isVisible = item.timeVisible
        }
    }

fun createConversationReceiveDocumentAdapterDelegate(onDocumentClicked: (String, String?, isDownloaded: (String?) -> Unit) -> Unit) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Document, ConversationItemReceiveDocumentBinding>(
        ConversationItemReceiveDocumentBinding::inflate
    ) {
        binding.messageContent.onClick {
            onDocumentClicked(item.content, item.path) {
                item.path = it
            }
        }
        bindWithBinding {
            fileName.text = item.content.substring(item.content.lastIndexOf(".") + 1).toUpperCase()
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
        }
    }

fun createConversationGroupTextAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onGroupMessageLongClicked: (ConversationViewItem.Group.Text) -> Unit
) =
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
            tvEdited.isVisible = item.isEdited
            username.setPrintableText(item.userName)
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
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
            sendTimeView.isVisible = item.timeVisible
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.common_avatar)
        }
    }

fun createConversationGroupDocumentAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onDocumentClicked: (String, String?, isDownloaded: (String?) -> Unit) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Document, ConversationItemGroupDocumentBinding>(
        ConversationItemGroupDocumentBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        binding.messageContent.onClick {
            onDocumentClicked(item.content, item.path) {
                item.path = it
            }
        }
        bindWithBinding {
            username.setPrintableText(item.userName)
            sendTimeView.setPrintableText(item.time)
            fileName.text = item.content.substring(item.content.lastIndexOf(".") + 1).toUpperCase()
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.common_avatar)
            sendTimeView.isVisible = item.timeVisible
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