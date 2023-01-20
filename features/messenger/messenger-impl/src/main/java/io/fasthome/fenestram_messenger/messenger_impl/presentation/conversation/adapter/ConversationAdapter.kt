package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import android.graphics.Rect
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.core.ui.extensions.loadRounded
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.*
import io.fasthome.fenestram_messenger.uikit.SpacingItemDecoration
import io.fasthome.fenestram_messenger.uikit.custom_view.SwipeRevealLayout
import io.fasthome.fenestram_messenger.uikit.custom_view.ViewBinderHelper
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.links.addCommonLinks
import io.fasthome.network.client.ProgressListener

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
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,

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

    //---Клики по документам---//
    onReceiveDocumentLongClicked: (ConversationViewItem.Receive) -> Unit,
    onSelfDocumentLongClicked: (ConversationViewItem.Self) -> Unit,
    onGroupDocumentLongClicked: (ConversationViewItem.Group) -> Unit,

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
                onDownloadDocument = onDownloadDocument,
                onReplyMessageDocument = onReplyMessage,
                onSelfDocumentLongClicked = onSelfDocumentLongClicked
            ),
            createConversationSelfTextReplyImageAdapterDelegate(
                onSelfTextReplyImageLongClicked = onSelfTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument
            ),
            createConversationSelfForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onSelfForwardLongClicked = onSelfForwardLongClicked,
                onImageClicked = onImageClicked,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument
            ),

            createConversationReceiveTextAdapterDelegate(
                onReceiveMessageLongClicked = onReceiveMessageLongClicked,
                onReplyMessage = onReplyMessage,
                viewBinderHelper = viewBinderHelper,
                onUserTagClicked = onUserTagClicked
            ),
            createConversationReceiveDocumentAdapterDelegate(
                onDownloadDocument = onDownloadDocument,
                onReplyMessageDocument = onReplyMessage,
                onReceiveDocumentLongClicked = onReceiveDocumentLongClicked
            ),

            createConversationReceiveForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onReceiveForwardLongClicked = onReceiveForwardLongClicked,
                onImageClicked = onImageClicked,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument
            ),
            createConversationReceiveTextReplyImageAdapterDelegate(
                onReceiveTextReplyImageLongClicked = onReceiveTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument
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
                onDownloadDocument = onDownloadDocument,
                onReplyMessageDocument = onReplyMessage,
                onGroupDocumentLongClicked = onGroupDocumentLongClicked
            ),
            createConversationGroupForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onGroupForwardLongClicked = onGroupForwardLongClicked,
                onImageClicked = onImageClicked,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument,
                onGroupProfileItemClicked = onGroupProfileItemClicked
            ),
            createConversationGroupTextReplyImageAdapterDelegate(
                onGroupTextReplyImageLongClicked = onGroupTextReplyImageLongClicked,
                onImageClicked = onImageClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onProfileClicked = onGroupProfileItemClicked,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument
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
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.TextReplyOnImage, ConversationItemSelfTextReplyImageBinding>(
        ConversationItemSelfTextReplyImageBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
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
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
                binding.replyDocumentContent.isVisible = false

            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                renderReply(
                    replyDocumentContent = replyDocumentContent,
                    replyMessageName = replyMessageName,
                    replyUserName = it.userName,
                    replyImage = replyImage
                )
                createDocumentAdapter(
                    recyclerViewDocuments = rvDocs,
                    onDownloadDocument = onDownloadDocument,
                    it.metaInfo
                )
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
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Forward, ConversationItemSelfForwardTextBinding>(
        ConversationItemSelfForwardTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
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
            val forwardMessage = item.forwardMessage
            when (forwardMessage) {
                is ConversationTextItem -> {
                    forwardAuthorName.setPrintableText(item.forwardMessage.userName)
                    messageContent.setPrintableText(item.forwardMessage.content as PrintableText)
                    messageContent.addCommonLinks(onUserTagClicked)
                    forwardDocumentContent.isVisible = false
                    forwardImage.isVisible = false
                    ivArrow.isVisible = true
                }
                is ConversationImageItem -> {
                    forwardAuthorName.text = getString(
                        R.string.forward_image_from_ph,
                        getPrintableRawText(item.forwardMessage.userName)
                    )
                    ivArrow.isVisible = false
                    messageContent.isVisible = false
                    forwardImage.isVisible = true
                    forwardDocumentContent.isVisible = false
                    forwardImage.loadRounded(item.forwardMessage.content as String)
                }
                is ConversationDocumentItem -> {
                    renderForward(
                        forwardDocumentContent = forwardDocumentContent,
                        forwardMessageName = forwardAuthorName,
                        forwardUserName = forwardMessage.userName,
                        forwardImage = forwardImage,
                        forwardText = messageContent
                    )
                    createDocumentAdapter(
                        recyclerViewDocuments = rvDocs,
                        onDownloadDocument = onDownloadDocument,
                        forwardMessage.metaInfo
                    )
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
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
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
        onReplyMessage = {
            if (item.sentStatus.canSwipe())
                onReplyMessageImage(item)
        },
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
        createImageAdapter(messageContent,{},item.metaInfo)
        //messageContent.loadRounded(item.metaInfo.first().url)
        sendTimeView.setPrintableText(item.time)
        sendTimeView.isVisible = item.timeVisible
        status.setImageResource(item.statusIcon)
        status.isVisible = item.timeVisible
    }
}

fun createConversationSelfDocumentAdapterDelegate(
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit,
    onSelfDocumentLongClicked: (ConversationViewItem.Self) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Self.Document, ConversationItemSelfDocumentBinding>(
        ConversationItemSelfDocumentBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessageDocument(item)
            },
        )
        binding.contentLayout.onClick {
            onSelfDocumentLongClicked(item)
        }
        bindWithBinding {
            createDocumentAdapter(
                recyclerViewDocuments = rvDocs,
                onDownloadDocument = onDownloadDocument,
                item.metaInfo
            )
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
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
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
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Forward, ConversationItemReceiveForwardTextBinding>(
        ConversationItemReceiveForwardTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
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
            val forwardMessage = item.forwardMessage
            when (forwardMessage) {
                is ConversationTextItem -> {
                    forwardAuthorName.setPrintableText(item.forwardMessage.userName)
                    messageContent.setPrintableText(item.forwardMessage.content as PrintableText)
                    messageContent.addCommonLinks(onUserTagClicked)
                    forwardDocumentContent.isVisible = false
                    forwardImage.isVisible = false
                    ivArrow.isVisible = true
                    messageContent.isVisible = true
                }
                is ConversationImageItem -> {
                    ivArrow.isVisible = false
                    messageContent.isVisible = false
                    forwardImage.isVisible = true
                    forwardDocumentContent.isVisible = false
                    forwardAuthorName.text = getString(
                        R.string.forward_image_from_ph,
                        getPrintableRawText(item.forwardMessage.userName)
                    )
                    forwardImage.loadRounded(item.forwardMessage.content as String)
                }
                is ConversationDocumentItem -> {
                    renderForward(
                        forwardDocumentContent = forwardDocumentContent,
                        forwardMessageName = forwardAuthorName,
                        forwardUserName = forwardMessage.userName,
                        forwardImage = forwardImage,
                        forwardText = messageContent
                    )
                    createDocumentAdapter(
                        recyclerViewDocuments = rvDocs,
                        onDownloadDocument = onDownloadDocument,
                        forwardMessage.metaInfo
                    )
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
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.TextReplyOnImage, ConversationItemReceiveTextReplyImageBinding>(
        ConversationItemReceiveTextReplyImageBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
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
                replyImage.isVisible = true
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
                binding.replyDocumentContent.isVisible = false

            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                renderReply(
                    replyDocumentContent = replyDocumentContent,
                    replyMessageName = replyMessageName,
                    replyUserName = it.userName,
                    replyImage = replyImage
                )
                createDocumentAdapter(
                    recyclerViewDocuments = rvDocs,
                    onDownloadDocument = onDownloadDocument,
                    it.metaInfo
                )
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
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessageImage(item)
            },
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
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit,
    onReceiveDocumentLongClicked: (ConversationViewItem.Receive) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Document, ConversationItemReceiveDocumentBinding>(
        ConversationItemReceiveDocumentBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessageDocument(item)
            },
        )
        binding.contentLayout.onClick {
            onReceiveDocumentLongClicked(item)
        }
        bindWithBinding {
            createDocumentAdapter(
                recyclerViewDocuments = rvDocs,
                onDownloadDocument = onDownloadDocument,
                item.metaInfo
            )
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
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.TextReplyOnImage, ConversationItemGroupTextReplyImageBinding>(
        ConversationItemGroupTextReplyImageBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
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
                replyImage.isVisible = true
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                replyImage.loadRounded(it.content, radius = 8)
                binding.replyDocumentContent.isVisible = false

            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                renderReply(
                    replyDocumentContent = replyDocumentContent,
                    replyMessageName = replyMessageName,
                    replyUserName = it.userName,
                    replyImage = replyImage
                )
                createDocumentAdapter(
                    recyclerViewDocuments = rvDocs,
                    onDownloadDocument = onDownloadDocument,
                    it.metaInfo
                )
            }
            username.setPrintableText(item.userName)
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.ic_avatar_placeholder)
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
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
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
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onImageClicked: (ConversationViewItem) -> Unit,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Forward, ConversationItemGroupForwardTextBinding>(
        ConversationItemGroupForwardTextBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessage(item)
            },
        )
        binding.contentLayout.onClick {
            onGroupForwardLongClicked(item)
        }
        binding.forwardImage.onClick {
            (item.forwardMessage as? ConversationImageItem)?.let {
                onImageClicked(item.forwardMessage)
            }
        }
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)
            val forwardMessage = item.forwardMessage
            when (forwardMessage) {
                is ConversationTextItem -> {
                    forwardAuthorName.setPrintableText(item.forwardMessage.userName)
                    messageContent.setPrintableText(item.forwardMessage.content as PrintableText)
                    messageContent.addCommonLinks(onUserTagClicked)
                    forwardDocumentContent.isVisible = false
                    forwardImage.isVisible = false
                    ivArrow.isVisible = true
                }
                is ConversationImageItem -> {
                    forwardAuthorName.text = getString(
                        R.string.forward_image_from_ph,
                        getPrintableRawText(item.forwardMessage.userName)
                    )
                    ivArrow.isVisible = false
                    forwardDocumentContent.isVisible = false
                    forwardDocumentContent.isVisible = false
                    forwardImage.isVisible = true
                    forwardImage.loadRounded(item.forwardMessage.content as String)
                }
                is ConversationDocumentItem -> {
                    renderForward(
                        forwardDocumentContent = forwardDocumentContent,
                        forwardMessageName = forwardAuthorName,
                        forwardUserName = forwardMessage.userName,
                        forwardImage = forwardImage,
                        forwardText = messageContent
                    )
                    createDocumentAdapter(
                        recyclerViewDocuments = rvDocs,
                        onDownloadDocument = onDownloadDocument,
                        forwardMessage.metaInfo
                    )
                }
            }
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.ic_avatar_placeholder)
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
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessageImage(item)
            },
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
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit,
    onGroupDocumentLongClicked: (ConversationViewItem.Group) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Document, ConversationItemGroupDocumentBinding>(
        ConversationItemGroupDocumentBinding::inflate
    ) {
        binding.root.setTouchListener(
            onReplyMessage = {
                if (item.sentStatus.canSwipe())
                    onReplyMessageDocument(item)
            },
        )
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
        }

        binding.contentLayout.onClick {
            onGroupDocumentLongClicked(item)
        }
        bindWithBinding {
            createDocumentAdapter(
                recyclerViewDocuments = rvDocs,
                onDownloadDocument = onDownloadDocument,
                item.metaInfo
            )
            username.setPrintableText(item.userName)
            sendTimeView.setPrintableText(item.time)
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

private fun renderForward(
    forwardDocumentContent: LinearLayout,
    forwardMessageName: TextView,
    forwardUserName: PrintableText,
    forwardImage: ImageView,
    forwardText: TextView,
) {
    forwardText.isVisible = false
    forwardImage.isVisible = false
    forwardDocumentContent.isVisible = true
    forwardMessageName.setPrintableText(
        PrintableText.StringResource(
            R.string.forward_file_from_ph,
            getPrintableRawText(forwardUserName)
        )
    )
}

private fun renderReply(
    replyDocumentContent: LinearLayout,
    replyMessageName: TextView,
    replyUserName: PrintableText,
    replyImage: ImageView,
) {
    replyImage.isVisible = false
    replyDocumentContent.isVisible = true
    replyMessageName.setPrintableText(
        PrintableText.StringResource(
            R.string.reply_document_ph,
            getPrintableRawText(replyUserName)
        )
    )
}

private fun createImageAdapter(
    imageRecyclerView: RecyclerView,
    onImageClicked: (ConversationViewItem) -> Unit,
    items: List<MetaInfo>,
) {
    val adapterImage = ConversationImageAdapter(
        onImageClick = {
            // TODO:!!!
        }
    )
    imageRecyclerView.adapter = adapterImage
    val flexboxManager = FlexboxLayoutManager(imageRecyclerView.context).apply {
        flexWrap = FlexWrap.WRAP
        alignItems = AlignItems.STRETCH
        flexDirection = FlexDirection.ROW
    }

    val glm = GridLayoutManager(imageRecyclerView.context, 3)
    glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (position % 3 == 2) {
                3
            } else when (position % 4) {
                1, 3 -> 1
                0, 2 -> 2
                else ->                     //never gonna happen
                    -1
            }
        }
    }
    imageRecyclerView.layoutManager = glm

    /*val linearLayoutManager =
        LinearLayoutManager(imageRecyclerView.context, LinearLayoutManager.VERTICAL, true)*/
   // imageRecyclerView.layoutManager = flexboxManager
    imageRecyclerView.itemAnimator = null
    imageRecyclerView.isNestedScrollingEnabled = false
    adapterImage.items = items
    imageRecyclerView.addItemDecoration(SpacingItemDecoration { index, itemCount ->
        Rect(
            2.dp,
            2.dp,
            2.dp,
            2.dp,
        )
    })
}

private fun createDocumentAdapter(
    recyclerViewDocuments: RecyclerView,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    items: List<MetaInfo>,
) {
    val adapterDocument = ConversationDocumentAdapter(
        onDownloadDocument = onDownloadDocument
    )
    recyclerViewDocuments.adapter = adapterDocument
    val linearLayoutManager =
        LinearLayoutManager(recyclerViewDocuments.context, LinearLayoutManager.VERTICAL, true)
    recyclerViewDocuments.layoutManager = linearLayoutManager
    recyclerViewDocuments.itemAnimator = null
    recyclerViewDocuments.isNestedScrollingEnabled = false
    adapterDocument.items = items
}

private fun SentStatus.canSwipe() = this != SentStatus.Error && this != SentStatus.Loading