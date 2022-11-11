package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationImageItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationTextItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.uikit.custom_view.SwipeRevealLayout
import io.fasthome.fenestram_messenger.uikit.custom_view.ViewBinderHelper
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.links.addCommonLinks
import io.fasthome.network.client.ProgressListener
import kotlinx.coroutines.delay

class ConversationAdapter(
    //---[ViewBinderHelper] Для ответа на сообщение по свайпу---//
    viewBinderHelper: ViewBinderHelper,

    //---Клик для открытия профиля собеседника в группе---//
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,

    //---Клик для открытия картинки---//
    onImageClicked: (String) -> Unit,

    //---Клики по сообщениям с документами---//
    onSelfDownloadDocument: (item: ConversationViewItem.Self.Document, progressListener: ProgressListener) -> Unit,
    onRecieveDownloadDocument: (item: ConversationViewItem.Receive.Document, progressListener: ProgressListener) -> Unit,
    onGroupDownloadDocument: (item: ConversationViewItem.Group.Document, progressListener: ProgressListener) -> Unit,

    //---Клики по сообщениям с изображениями---//
    onSelfImageLongClicked: (ConversationViewItem.Self.Image) -> Unit,
    onReceiveImageLongClicked: (ConversationViewItem.Receive.Image) -> Unit,
    onGroupImageLongClicked: (ConversationViewItem.Group.Image) -> Unit,

    //---Клики по текстовым сообщениям---//
    onSelfMessageLongClicked: (ConversationViewItem.Self.Text) -> Unit,
    onReceiveMessageLongClicked: (ConversationViewItem.Receive.Text) -> Unit,
    onGroupMessageLongClicked: (ConversationViewItem.Group.Text) -> Unit,

    //---Клики по пересланным сообщениям---//
    onSelfTextReplyImageLongClicked: (item: ConversationViewItem.Self.TextReplyOnImage) -> Unit,
    onReceiveTextReplyImageLongClicked: (item: ConversationViewItem.Receive.TextReplyOnImage) -> Unit,
    onGroupTextReplyImageLongClicked: (item: ConversationViewItem.Group.TextReplyOnImage) -> Unit,

    //---Ответы---//
    onReplyMessageText: (item: ConversationViewItem) -> Unit,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit,
) :
    AsyncListDifferDelegationAdapter<ConversationViewItem>(
        AdapterUtil.diffUtilItemCallbackEquals(
            ConversationViewItem::id,
            ConversationViewItem::sentStatus,
            ConversationViewItem::timeVisible,
        ),
        AdapterUtil.adapterDelegatesManager(
            createConversationSelfTextAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessageText,
                onSelfMessageLongClicked = onSelfMessageLongClicked
            ),
            createConversationSelfImageAdapterDelegate(
                onImageClicked = onImageClicked,
                onSelfImageLongClicked = onSelfImageLongClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessageImage = onReplyMessageImage
            ),
            createConversationSelfDocumentAdapterDelegate(
                onDownloadDocument = onSelfDownloadDocument
            ),
            createConversationSelfTextReplyImageAdapterDelegate(
                onSelfTextReplyImageLongClicked = onSelfTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessageImage
            ),

            createConversationReceiveTextAdapterDelegate(
                onReceiveMessageLongClicked = onReceiveMessageLongClicked,
                onReplyMessage = onReplyMessageText,
                viewBinderHelper = viewBinderHelper
            ),
            createConversationReceiveDocumentAdapterDelegate(onDownloadDocument = onRecieveDownloadDocument),
            createConversationReceiveTextReplyImageAdapterDelegate(
                onReceiveTextReplyImageLongClicked = onReceiveTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessageImage
            ),
            createConversationReceiveImageAdapterDelegate(
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReceiveImageLongClicked = onReceiveImageLongClicked,
                onReplyMessageImage = onReplyMessageImage
            ),

            createConversationGroupTextAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onGroupMessageLongClicked = onGroupMessageLongClicked,
                onReplyMessage = onReplyMessageText,
                viewBinderHelper = viewBinderHelper
            ),
            createConversationGroupImageAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessageImage = onReplyMessageImage,
                onGroupImageLongClicked = onGroupImageLongClicked
            ),
            createConversationGroupDocumentAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onDownloadDocument = onGroupDownloadDocument
            ),
            createConversationGroupTextReplyImageAdapterDelegate(
                onGroupTextReplyImageLongClicked = onGroupTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessageImage,
                onProfileClicked = onGroupProfileItemClicked
            ),

            createConversationSystemAdapterDelegate(),
        )
    )

//---Адаптеры для своих сообщений---//

fun createConversationSelfTextReplyImageAdapterDelegate(
    onSelfTextReplyImageLongClicked: (ConversationViewItem.Self.TextReplyOnImage) -> Unit,
    onImageClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.TextReplyOnImage, ConversationItemSelfTextReplyImageBinding>(
        ConversationItemSelfTextReplyImageBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onSelfTextReplyImageLongClicked(item)
        }
        binding.replyImage.onClick {
            (item.replyMessage as? ConversationImageItem)?.let {
                onImageClicked(it.content)
            }
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                replyMessageName.text = context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
            }

            tvEdited.isVisible = item.isEdited
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationSelfTextAdapterDelegate(
    onSelfMessageLongClicked: (ConversationViewItem.Self.Text) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Text, ConversationItemSelfTextBinding>(
        ConversationItemSelfTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onSelfMessageLongClicked(item)
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)
            val replyMessage = item.replyMessage as? ConversationTextItem
            clReplyMessage.isVisible = replyMessage != null
            if (replyMessage != null) {
                replyAuthorName.text = getPrintableRawText(replyMessage.userName)
                replyContent.text = getPrintableRawText(replyMessage.content)
            }
            tvEdited.isVisible = item.isEdited
            messageContent.setPrintableText(item.content)
            messageContent.addCommonLinks()
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationSelfImageAdapterDelegate(
    onImageClicked: (String) -> Unit,
    onSelfImageLongClicked: (ConversationViewItem.Self.Image) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit
) = adapterDelegateViewBinding<ConversationViewItem.Self.Image, ConversationItemSelfImageBinding>(
    ConversationItemSelfImageBinding::inflate
) {
    binding.root.setTouchListener(
        onReplyMessage = { onReplyMessageImage(item) },
    )
    binding.contentLayout.onClick {
        onSelfImageLongClicked(item)
    }
    binding.messageContent.onClick {
        onImageClicked(item.content)
    }
    bindWithBinding {
        viewBinderHelper.bind(root, item.id.toString())
        viewBinderHelper.setOpenOnlyOne(true)

        messageContent.loadRounded(item.content)
        sendTimeView.setPrintableText(item.time)
        sendTimeView.isVisible = item.timeVisible
        status.setImageResource(item.statusIcon)
        status.isVisible = item.timeVisible
    }
}

fun createConversationSelfDocumentAdapterDelegate(
    onDownloadDocument: (item: ConversationViewItem.Self.Document, progressListener: ProgressListener) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Document, ConversationItemSelfDocumentBinding>(
        ConversationItemSelfDocumentBinding::inflate
    ) {
        binding.messageContent.onClick {
            binding.progressBar.isInvisible = false
            onDownloadDocument(item) {
                binding.progressBar.progress = it

                if (it == 100) delay(400)
                binding.progressBar.isInvisible = it == 100
            }
        }
        bindWithBinding {
            binding.progressBar.isInvisible = true
            fileName.text = item.content.substring(item.content.lastIndexOf(".") + 1).toUpperCase()
            sendTimeView.setPrintableText(item.time)
            status.setImageResource(item.statusIcon)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
        }
    }


//---Адаптеры для принятых личных сообщений---//

fun createConversationReceiveTextAdapterDelegate(
    onReceiveMessageLongClicked: (ConversationViewItem.Receive.Text) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Text, ConversationItemReceiveTextBinding>(
        ConversationItemReceiveTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onReceiveMessageLongClicked(item)
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            val replyMessage = item.replyMessage as? ConversationTextItem
            if (replyMessage != null) {
                clReplyMessage.isVisible = true
                replyAuthorName.text = getPrintableRawText(replyMessage.userName)
                replyContent.text = getPrintableRawText(replyMessage.content)
            } else {
                clReplyMessage.isVisible = false
            }
            tvEdited.isVisible = item.isEdited
            messageContent.setPrintableText(item.content)
            messageContent.addCommonLinks()
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
        }
    }

fun createConversationReceiveTextReplyImageAdapterDelegate(
    onReceiveTextReplyImageLongClicked: (ConversationViewItem.Receive.TextReplyOnImage) -> Unit,
    onImageClicked: (String) -> Unit,
    onReplyMessage: (ConversationViewItem) -> Unit,
    viewBinderHelper: ViewBinderHelper,
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.TextReplyOnImage, ConversationItemReceiveTextReplyImageBinding>(
        ConversationItemReceiveTextReplyImageBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onReceiveTextReplyImageLongClicked(item)
        }
        binding.replyImage.onClick {
            (item.replyMessage as? ConversationImageItem)?.let {
                onImageClicked(it.content)
            }
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                replyMessageName.text = context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
            }
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
        }
    }


fun createConversationReceiveImageAdapterDelegate(
    onImageClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReceiveImageLongClicked: (ConversationViewItem.Receive.Image) -> Unit,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit
) = adapterDelegateViewBinding<ConversationViewItem.Receive.Image, ConversationItemReceiveImageBinding>(
    ConversationItemReceiveImageBinding::inflate
) {
    binding.messageContent.onClick {
        onImageClicked(item.content)
    }
    binding.root.setTouchListener(
        onReplyMessage = { onReplyMessageImage(item) },
    )
    binding.contentLayout.onClick {
        onReceiveImageLongClicked(item)
    }
    bindWithBinding {
        viewBinderHelper.bind(root, item.id.toString())
        viewBinderHelper.setOpenOnlyOne(true)

        messageContent.loadRounded(item.content)
        sendTimeView.setPrintableText(item.time)
        sendTimeView.isVisible = item.timeVisible
    }
}

fun createConversationReceiveDocumentAdapterDelegate(
    onDownloadDocument: (item: ConversationViewItem.Receive.Document, progressListener: ProgressListener) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Document, ConversationItemReceiveDocumentBinding>(
        ConversationItemReceiveDocumentBinding::inflate
    ) {
        binding.messageContent.onClick {
            binding.progressBar.isInvisible = false
            onDownloadDocument(item) {
                binding.progressBar.progress = it

                if (it == 100) delay(400)
                binding.progressBar.isInvisible = it == 100
            }
        }
        bindWithBinding {
            binding.progressBar.isInvisible = true
            fileName.text = item.content.substring(item.content.lastIndexOf(".") + 1).toUpperCase()
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
        }
    }


//---Адаптеры для принятых групповых сообщений---//

fun createConversationGroupTextReplyImageAdapterDelegate(
    onGroupTextReplyImageLongClicked: (ConversationViewItem.Group.TextReplyOnImage) -> Unit,
    onImageClicked: (String) -> Unit,
    onReplyMessage: (ConversationViewItem) -> Unit,
    onProfileClicked: (ConversationViewItem.Group) -> Unit,
    viewBinderHelper: ViewBinderHelper,
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.TextReplyOnImage, ConversationItemGroupTextReplyImageBinding>(
        ConversationItemGroupTextReplyImageBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onGroupTextReplyImageLongClicked(item)
        }
        binding.replyImage.onClick {
            (item.replyMessage as? ConversationImageItem)?.let {
                onImageClicked(it.content)
            }
        }
        binding.avatar.onClick {
            onProfileClicked(item)
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                replyMessageName.text = context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
            }
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
        }
    }

fun createConversationGroupTextAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onGroupMessageLongClicked: (ConversationViewItem.Group.Text) -> Unit,
    onReplyMessage: (ConversationViewItem) -> Unit,
    viewBinderHelper: ViewBinderHelper
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Text, ConversationItemGroupTextBinding>(
        ConversationItemGroupTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onGroupMessageLongClicked(item)
        }
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            val replyMessage = item.replyMessage as? ConversationTextItem
            if (replyMessage != null) {
                clReplyMessage.isVisible = true
                replyAuthorName.text = getPrintableRawText(replyMessage.userName)
                replyContent.text = getPrintableRawText(replyMessage.content)
            } else {
                clReplyMessage.isVisible = false
            }

            tvEdited.isVisible = item.isEdited
            username.setPrintableText(item.userName)
            messageContent.setPrintableText(item.content)
            messageContent.addCommonLinks()
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.ic_avatar_placeholder)
        }
    }

fun createConversationGroupImageAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onImageClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit,
    onGroupImageLongClicked: (ConversationViewItem.Group.Image) -> Unit
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
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessageImage(item) },
        )
        binding.contentLayout.onClick {
            onGroupImageLongClicked(item)
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)
            username.setPrintableText(item.userName)
            messageContent.loadRounded(item.content)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.ic_avatar_placeholder)
        }
    }

fun createConversationGroupDocumentAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onDownloadDocument: (item: ConversationViewItem.Group.Document, progressListener: ProgressListener) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Document, ConversationItemGroupDocumentBinding>(
        ConversationItemGroupDocumentBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        binding.messageContent.onClick {
            binding.progressBar.isInvisible = false
            onDownloadDocument(item) {
                binding.progressBar.progress = it

                if (it == 100) delay(400)
                binding.progressBar.isInvisible = it == 100
            }
        }
        bindWithBinding {
            binding.progressBar.isInvisible = true
            username.setPrintableText(item.userName)
            sendTimeView.setPrintableText(item.time)
            fileName.text = item.content.substring(item.content.lastIndexOf(".") + 1).toUpperCase()
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.ic_avatar_placeholder)
            sendTimeView.isVisible = item.timeVisible
        }
    }


//---Системные адаптеры---//

fun createConversationSystemAdapterDelegate() =
    adapterDelegateViewBinding<ConversationViewItem.System, ConversationItemSystemBinding>(
        ConversationItemSystemBinding::inflate
    ) {
        bindWithBinding {
            date.setPrintableText(item.content)
        }
    }

private fun SwipeRevealLayout.setTouchListener(
    onReplyMessage: () -> Unit,
    onClick: (() -> Unit?)? = null,
) {
    setNeedCloseOnActionPoinerUp(true)
    setSwipeListener(object : SwipeRevealLayout.SimpleSwipeListener() {
        override fun onOpened(view: SwipeRevealLayout?) {
            context.vibrate()
        }

        override fun onLetGo() {
            close(true)
            onReplyMessage()
        }

        override fun onClick() {
            onClick?.invoke()
        }
    })
}