package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationDocumentItem
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
    //---Клик для открытия профиля по нажатию на обращение к пользователю---//
    onUserTagClicked: (String) -> Unit,

    //---[ViewBinderHelper] Для ответа на сообщение по свайпу---//
    viewBinderHelper: ViewBinderHelper,

    //---Клик для открытия профиля собеседника в группе---//
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,

    //---Клик для открытия картинки---//
    onImageClicked: (ConversationViewItem) -> Unit,

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

    //---Клики по пересланным сообщениям
    onSelfForwardLongClicked: (ConversationViewItem.Self.Forward) -> Unit,
    onReceiveForwardLongClicked: (ConversationViewItem.Receive.Forward) -> Unit,
    onGroupForwardLongClicked: (ConversationViewItem.Group.Forward) -> Unit,

    //---Клики по ответам на сообщения---//
    onSelfTextReplyImageLongClicked: (item: ConversationViewItem.Self.TextReplyOnImage) -> Unit,
    onReceiveTextReplyImageLongClicked: (item: ConversationViewItem.Receive.TextReplyOnImage) -> Unit,
    onGroupTextReplyImageLongClicked: (item: ConversationViewItem.Group.TextReplyOnImage) -> Unit,

    //---Ответ---//
    onReplyMessage: (item: ConversationViewItem) -> Unit,
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
                onReplyMessage = onReplyMessage,
                onSelfMessageLongClicked = onSelfMessageLongClicked,
                onUserTagClicked = onUserTagClicked
            ),
            createConversationSelfImageAdapterDelegate(
                onImageClicked = onImageClicked,
                onSelfImageLongClicked = onSelfImageLongClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessageImage = onReplyMessage
            ),
            createConversationSelfDocumentAdapterDelegate(
                onDownloadDocument = onSelfDownloadDocument,
                onReplyMessageDocument = onReplyMessage
            ),
            createConversationSelfTextReplyImageAdapterDelegate(
                onSelfTextReplyImageLongClicked = onSelfTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onUserTagClicked = onUserTagClicked
            ),
            createConversationSelfForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onSelfForwardLongClicked = onSelfForwardLongClicked,
                onImageClicked = onImageClicked,
                onUserTagClicked = onUserTagClicked
            ),

            createConversationReceiveTextAdapterDelegate(
                onReceiveMessageLongClicked = onReceiveMessageLongClicked,
                onReplyMessage = onReplyMessage,
                viewBinderHelper = viewBinderHelper,
                onUserTagClicked = onUserTagClicked
            ),
            createConversationReceiveDocumentAdapterDelegate(
                onDownloadDocument = onRecieveDownloadDocument,
                onReplyMessageDocument = onReplyMessage),

            createConversationReceiveForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onReceiveForwardLongClicked = onReceiveForwardLongClicked,
                onImageClicked = onImageClicked,
                onUserTagClicked = onUserTagClicked
            ),
            createConversationReceiveTextReplyImageAdapterDelegate(
                onReceiveTextReplyImageLongClicked = onReceiveTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onUserTagClicked = onUserTagClicked
            ),
            createConversationReceiveImageAdapterDelegate(
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReceiveImageLongClicked = onReceiveImageLongClicked,
                onReplyMessageImage = onReplyMessage
            ),

            createConversationGroupTextAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onGroupMessageLongClicked = onGroupMessageLongClicked,
                onReplyMessage = onReplyMessage,
                viewBinderHelper = viewBinderHelper,
                onUserTagClicked = onUserTagClicked
            ),
            createConversationGroupImageAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessageImage = onReplyMessage,
                onGroupImageLongClicked = onGroupImageLongClicked
            ),
            createConversationGroupDocumentAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onDownloadDocument = onGroupDownloadDocument,
                onReplyMessageDocument = onReplyMessage
            ),
            createConversationGroupForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onGroupForwardLongClicked = onGroupForwardLongClicked,
                onImageClicked = onImageClicked,
                onUserTagClicked = onUserTagClicked
            ),
            createConversationGroupTextReplyImageAdapterDelegate(
                onGroupTextReplyImageLongClicked = onGroupTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onProfileClicked = onGroupProfileItemClicked,
                onUserTagClicked = onUserTagClicked
            ),

            createConversationSystemAdapterDelegate(),
        )
    )

//---Адаптеры для своих сообщений---//

fun createConversationSelfTextReplyImageAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onSelfTextReplyImageLongClicked: (ConversationViewItem.Self.TextReplyOnImage) -> Unit,
    onImageClicked: (ConversationViewItem) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit,
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
                onImageClicked(item)
            }
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                replyImage.isVisible = true
                fileName.isVisible = false
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                replyImage.isVisible = false
                fileName.isVisible = true
                fileName.text = it.metaInfo?.name
                replyMessageName.text =
                    context.getString(R.string.reply_document_ph, getPrintableRawText(it.userName))
            }

            tvEdited.isVisible = item.isEdited
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            messageContent.addCommonLinks(onUserTagClicked)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationSelfForwardAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (ConversationViewItem) -> Unit,
    onSelfForwardLongClicked: (ConversationViewItem.Self.Forward) -> Unit,
    onImageClicked: (ConversationViewItem) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Forward, ConversationItemSelfForwardTextBinding>(
        ConversationItemSelfForwardTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onSelfForwardLongClicked(item)
        }
        binding.forwardImage.onClick {
            (item.forwardMessage as? ConversationImageItem)?.let {
                onImageClicked(item.forwardMessage)
            }
        }
        bindWithBinding {
            when (item.forwardMessage) {
                is ConversationTextItem -> {
                    forwardAuthorName.setPrintableText(item.forwardMessage.userName)
                    messageContent.setPrintableText(item.forwardMessage.content as PrintableText)
                    messageContent.addCommonLinks(onUserTagClicked)
                }
                is ConversationImageItem -> {
                    forwardAuthorName.text = getString(R.string.reply_image_from_ph,
                        getPrintableRawText(item.forwardMessage.userName))
                    ivArrow.isVisible = false
                    messageContent.isVisible = false
                    forwardImage.isVisible = true
                    forwardImage.loadRounded(item.forwardMessage.content as String)
                }
            }
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationSelfTextAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onSelfMessageLongClicked: (ConversationViewItem.Self.Text) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit,
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
            messageContent.addCommonLinks(onUserTagClicked)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
            status.setImageResource(item.statusIcon)
        }
    }

fun createConversationSelfImageAdapterDelegate(
    onImageClicked: (ConversationViewItem) -> Unit,
    onSelfImageLongClicked: (ConversationViewItem.Self.Image) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit,
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
        onImageClicked(item)
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
    onDownloadDocument: (item: ConversationViewItem.Self.Document, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Document, ConversationItemSelfDocumentBinding>(
        ConversationItemSelfDocumentBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessageDocument(item) }
        )
        binding.messageContent.onClick {
            binding.progressBar.isVisible = true
            onDownloadDocument(item) { progress ->
                binding.progressBar.progress = progress
                binding.progressBar.isVisible = item.metaInfo != null
                binding.fileName.isVisible = item.metaInfo != null
                item.metaInfo?.let { meta ->
                    binding.fileSize.text = getString(R.string.meta_document_size_ph,
                        meta.size * progress / 100,
                        meta.size)
                }
                if (progress == 100) delay(400)
                binding.progressBar.isVisible = progress != 100
                binding.fileSize.isVisible = progress != 100
            }
        }
        bindWithBinding {
            fileSize.text = getString(R.string.mb_real, item.metaInfo?.size ?: 0f)
            binding.progressBar.isInvisible = true
            fileName.text =
                item.metaInfo?.name //item.content.substring(item.content.lastIndexOf(".") + 1).toUpperCase()
            sendTimeView.setPrintableText(item.time)
            status.setImageResource(item.statusIcon)
            sendTimeView.isVisible = item.timeVisible
            status.isVisible = item.timeVisible
        }
    }


//---Адаптеры для принятых личных сообщений---//

fun createConversationReceiveTextAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onReceiveMessageLongClicked: (ConversationViewItem.Receive.Text) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit,
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
            messageContent.addCommonLinks(onUserTagClicked)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
        }
    }

fun createConversationReceiveForwardAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (ConversationViewItem) -> Unit,
    onReceiveForwardLongClicked: (ConversationViewItem.Receive.Forward) -> Unit,
    onImageClicked: (ConversationViewItem) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Forward, ConversationItemReceiveForwardTextBinding>(
        ConversationItemReceiveForwardTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onReceiveForwardLongClicked(item)
        }
        binding.forwardImage.onClick {
            (item.forwardMessage as? ConversationImageItem)?.let {
                onImageClicked(item.forwardMessage)
            }
        }
        bindWithBinding {
            when (item.forwardMessage) {
                is ConversationTextItem -> {
                    forwardAuthorName.setPrintableText(item.forwardMessage.userName)
                    messageContent.setPrintableText(item.forwardMessage.content as PrintableText)
                    messageContent.addCommonLinks(onUserTagClicked)
                }
                is ConversationImageItem -> {
                    ivArrow.isVisible = false
                    messageContent.isVisible = false
                    forwardImage.isVisible = true
                    forwardAuthorName.text = getString(R.string.reply_image_from_ph,
                        getPrintableRawText(item.forwardMessage.userName))
                    forwardImage.loadRounded(item.forwardMessage.content as String)
                }
            }
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)
            username.text = getPrintableRawText(item.userName)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
        }
    }

fun createConversationReceiveTextReplyImageAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onReceiveTextReplyImageLongClicked: (ConversationViewItem.Receive.TextReplyOnImage) -> Unit,
    onImageClicked: (ConversationViewItem) -> Unit,
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
                onImageClicked(item)
            }
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                replyImage.isVisible = false
                fileName.isVisible = true
                fileName.text = it.metaInfo?.name
                replyMessageName.text =
                    context.getString(R.string.reply_document_ph, getPrintableRawText(it.userName))
            }
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            messageContent.addCommonLinks(onUserTagClicked)
            sendTimeView.isVisible = item.timeVisible
        }
    }


fun createConversationReceiveImageAdapterDelegate(
    onImageClicked: (ConversationViewItem) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReceiveImageLongClicked: (ConversationViewItem.Receive.Image) -> Unit,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Image, ConversationItemReceiveImageBinding>(
        ConversationItemReceiveImageBinding::inflate
    ) {
        binding.messageContent.onClick {
            onImageClicked(item)
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
    onDownloadDocument: (item: ConversationViewItem.Receive.Document, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Document, ConversationItemReceiveDocumentBinding>(
        ConversationItemReceiveDocumentBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessageDocument(item) }
        )
        binding.messageContent.onClick {
            binding.progressBar.isVisible = true
            onDownloadDocument(item) { progress ->
                binding.progressBar.progress = progress
                binding.progressBar.isVisible = item.metaInfo != null
                binding.fileName.isVisible = item.metaInfo != null
                item.metaInfo?.let { meta ->
                    binding.fileSize.text = getString(R.string.meta_document_size_ph,
                        meta.size * progress / 100,
                        meta.size)
                }
                if (progress == 100) delay(400)
                binding.progressBar.isVisible = progress != 100
                binding.fileSize.isVisible = progress != 100
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
    onUserTagClicked: (String) -> Unit,
    onGroupTextReplyImageLongClicked: (ConversationViewItem.Group.TextReplyOnImage) -> Unit,
    onImageClicked: (ConversationViewItem) -> Unit,
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
                onImageClicked(item)
            }
        }
        binding.avatar.onClick {
            onProfileClicked(item)
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                replyImage.isVisible = false
                fileName.isVisible = true
                fileName.text = it.metaInfo?.name
                replyMessageName.text =
                    context.getString(R.string.reply_document_ph, getPrintableRawText(it.userName))
            }
            messageContent.setPrintableText(item.content)
            sendTimeView.setPrintableText(item.time)
            messageContent.addCommonLinks(onUserTagClicked)
            sendTimeView.isVisible = item.timeVisible
        }
    }

fun createConversationGroupTextAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onGroupMessageLongClicked: (ConversationViewItem.Group.Text) -> Unit,
    onReplyMessage: (ConversationViewItem) -> Unit,
    viewBinderHelper: ViewBinderHelper,
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
            messageContent.addCommonLinks(onUserTagClicked)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.ic_avatar_placeholder)
        }
    }


fun createConversationGroupForwardAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (ConversationViewItem) -> Unit,
    onGroupForwardLongClicked: (ConversationViewItem.Group.Forward) -> Unit,
    onImageClicked: (ConversationViewItem) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Forward, ConversationItemGroupForwardTextBinding>(
        ConversationItemGroupForwardTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessage(item) },
        )
        binding.contentLayout.onClick {
            onGroupForwardLongClicked(item)
        }
        binding.forwardImage.onClick {
            (item.forwardMessage as? ConversationImageItem)?.let {
                onImageClicked(item.forwardMessage)
            }
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)
            when (item.forwardMessage) {
                is ConversationTextItem -> {
                    forwardAuthorName.setPrintableText(item.forwardMessage.userName)
                    messageContent.setPrintableText(item.forwardMessage.content as PrintableText)
                    messageContent.addCommonLinks(onUserTagClicked)
                }
                is ConversationImageItem -> {
                    forwardAuthorName.text = getString(R.string.reply_image_from_ph,
                        getPrintableRawText(item.forwardMessage.userName))
                    ivArrow.isVisible = false
                    messageContent.isVisible = false
                    forwardImage.isVisible = true
                    forwardImage.loadRounded(item.forwardMessage.content as String)
                }
            }

            username.setPrintableText(item.userName)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
        }
    }

fun createConversationGroupImageAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onImageClicked: (ConversationViewItem) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit,
    onGroupImageLongClicked: (ConversationViewItem.Group.Image) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Image, ConversationItemGroupImageBinding>(
        ConversationItemGroupImageBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        binding.messageContent.onClick {
            onImageClicked(item)
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
    onDownloadDocument: (item: ConversationViewItem.Group.Document, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Document, ConversationItemGroupDocumentBinding>(
        ConversationItemGroupDocumentBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = { onReplyMessageDocument(item) }
        )
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        binding.messageContent.onClick {
            binding.progressBar.isVisible = true
            onDownloadDocument(item) { progress ->
                binding.progressBar.progress = progress
                binding.progressBar.isVisible = item.metaInfo != null
                binding.fileName.isVisible = item.metaInfo != null
                item.metaInfo?.let { meta ->
                    binding.fileSize.text = getString(R.string.meta_document_size_ph,
                        meta.size * progress / 100,
                        meta.size)
                }
                if (progress == 100) delay(400)
                binding.progressBar.isVisible = progress != 100
                binding.fileSize.isVisible = progress != 100
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