package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter

import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import io.fasthome.fenestram_messenger.core.ui.extensions.loadCircle
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.imageAdapter.ConversationImageAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.adapter.imageAdapter.createAdapterItems
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.*
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
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,

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

    //---Клики по реакциям---//
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
                onUserTagClicked = onUserTagClicked,
                onReactionClicked = onReactionClicked
            ),
            createConversationSelfImageAdapterDelegate(
                onImageClicked = onImagesClicked,
                onSelfImageLongClicked = onSelfImageLongClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessageImage = onReplyMessage,
                onReactionClicked = onReactionClicked
            ),
            createConversationSelfDocumentAdapterDelegate(
                onDownloadDocument = onDownloadDocument,
                onReplyMessageDocument = onReplyMessage,
                onSelfDocumentLongClicked = onSelfDocumentLongClicked,
                onReactionClicked = onReactionClicked
            ),
            createConversationSelfTextReplyImageAdapterDelegate(
                onSelfTextReplyImageLongClicked = onSelfTextReplyImageLongClicked,
                onImagesClicked = onImagesClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument,
                onReactionClicked = onReactionClicked
            ),
            createConversationSelfForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onSelfForwardLongClicked = onSelfForwardLongClicked,
                onImagesClicked = onImagesClicked,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument,
                onReactionClicked = onReactionClicked
            ),

            createConversationReceiveTextAdapterDelegate(
                onReceiveMessageLongClicked = onReceiveMessageLongClicked,
                onReplyMessage = onReplyMessage,
                viewBinderHelper = viewBinderHelper,
                onUserTagClicked = onUserTagClicked,
                onReactionClicked = onReactionClicked
            ),
            createConversationReceiveDocumentAdapterDelegate(
                onDownloadDocument = onDownloadDocument,
                onReplyMessageDocument = onReplyMessage,
                onReceiveDocumentLongClicked = onReceiveDocumentLongClicked,
                onReactionClicked = onReactionClicked
            ),
            createConversationReceiveForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onReceiveForwardLongClicked = onReceiveForwardLongClicked,
                onImagesClicked = onImagesClicked,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument,
                onReactionClicked = onReactionClicked
            ),
            createConversationReceiveTextReplyImageAdapterDelegate(
                onReceiveTextReplyImageLongClicked = onReceiveTextReplyImageLongClicked,
                onImagesClicked = onImagesClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument,
                onReactionClicked = onReactionClicked
            ),
            createConversationReceiveImageAdapterDelegate(
                onImagesClicked = onImagesClicked,
                viewBinderHelper = viewBinderHelper,
                onReceiveImageLongClicked = onReceiveImageLongClicked,
                onReplyMessageImage = onReplyMessage,
                onReactionClicked = onReactionClicked
            ),

            createConversationGroupTextAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onGroupMessageLongClicked = onGroupMessageLongClicked,
                onReplyMessage = onReplyMessage,
                viewBinderHelper = viewBinderHelper,
                onUserTagClicked = onUserTagClicked,
                onReactionClicked = onReactionClicked
            ),
            createConversationGroupImageAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onImagesClicked = onImagesClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessageImage = onReplyMessage,
                onGroupImageLongClicked = onGroupImageLongClicked,
                onReactionClicked = onReactionClicked
            ),
            createConversationGroupDocumentAdapterDelegate(
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onDownloadDocument = onDownloadDocument,
                onReplyMessageDocument = onReplyMessage,
                onGroupDocumentLongClicked = onGroupDocumentLongClicked,
                onReactionClicked = onReactionClicked
            ),
            createConversationGroupForwardAdapterDelegate(
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onGroupForwardLongClicked = onGroupForwardLongClicked,
                onImagesClicked = onImagesClicked,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument,
                onGroupProfileItemClicked = onGroupProfileItemClicked,
                onReactionClicked = onReactionClicked
            ),
            createConversationGroupTextReplyImageAdapterDelegate(
                onGroupTextReplyImageLongClicked = onGroupTextReplyImageLongClicked,
                onImagesClicked = onImagesClicked,
                viewBinderHelper = viewBinderHelper,
                onReplyMessage = onReplyMessage,
                onProfileClicked = onGroupProfileItemClicked,
                onUserTagClicked = onUserTagClicked,
                onDownloadDocument = onDownloadDocument,
                onReactionClicked = onReactionClicked
            ),

            createConversationSystemAdapterDelegate(),
        )
    )

//---Адаптеры для своих сообщений---//

fun createConversationSelfTextReplyImageAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onSelfTextReplyImageLongClicked: (ConversationViewItem.Self.TextReplyOnImage) -> Unit,
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                rvImages.isVisible = true
                createImageAdapter(
                    imageRecyclerView = rvImages,
                    items = it.metaInfo,
                    onImageClicked = { imageMeta, pos ->
                        onImagesClicked(imageMeta, pos)
                    })
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                binding.replyDocumentContent.isVisible = false

            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                renderReply(
                    replyDocumentContent = replyDocumentContent,
                    replyMessageName = replyMessageName,
                    replyUserName = it.userName,
                    replyImages = rvImages
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationSelfForwardAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (ConversationViewItem) -> Unit,
    onSelfForwardLongClicked: (ConversationViewItem.Self.Forward) -> Unit,
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
        bindWithBinding {
            val forwardMessage = item.forwardMessage
            when (forwardMessage) {
                is ConversationTextItem -> {
                    forwardAuthorName.setPrintableText(item.forwardMessage.userName)
                    messageContent.setPrintableText(item.forwardMessage.content as PrintableText)
                    messageContent.addCommonLinks(onUserTagClicked)
                    forwardDocumentContent.isVisible = false
                    rvImages.isVisible = false
                    ivArrow.isVisible = true
                }
                is ConversationImageItem -> {
                    forwardAuthorName.text = getString(
                        R.string.forward_image_from_ph,
                        getPrintableRawText(item.forwardMessage.userName)
                    )
                    ivArrow.isVisible = false
                    messageContent.isVisible = false
                    rvImages.isVisible = true
                    createImageAdapter(
                        imageRecyclerView = rvImages,
                        items = forwardMessage.metaInfo,
                        onImageClicked = { imageMeta, pos ->
                            onImagesClicked(imageMeta, pos)
                        })
                    forwardDocumentContent.isVisible = false
                }
                is ConversationDocumentItem -> {
                    renderForward(
                        forwardDocumentContent = forwardDocumentContent,
                        forwardMessageName = forwardAuthorName,
                        forwardUserName = forwardMessage.userName,
                        forwardImages = rvImages,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationSelfTextAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onSelfMessageLongClicked: (ConversationViewItem.Self.Text) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationSelfImageAdapterDelegate(
    onImageClicked: (List<MetaInfo>, Int) -> Unit,
    onSelfImageLongClicked: (ConversationViewItem.Self.Image) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
    bindWithBinding {
        viewBinderHelper.bind(root, item.id.toString())
        viewBinderHelper.setOpenOnlyOne(true)
        createImageAdapter(
            imageRecyclerView = messageContent,
            items = item.metaInfo,
            onImageClicked = { imageMeta, pos ->
                onImageClicked(imageMeta, pos)
            })
        sendTimeView.setPrintableText(item.time)
        sendTimeView.isVisible = item.timeVisible
        status.setImageResource(item.statusIcon)
        status.isVisible = item.timeVisible
        createReactionsAdapter(listReactions, item.reactions) {
            onReactionClicked(item.id, it)
        }
    }
}

fun createConversationSelfDocumentAdapterDelegate(
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit,
    onSelfDocumentLongClicked: (ConversationViewItem.Self) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }


//---Адаптеры для принятых личных сообщений---//

fun createConversationReceiveTextAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onReceiveMessageLongClicked: (ConversationViewItem.Receive.Text) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (item: ConversationViewItem) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationReceiveForwardAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (ConversationViewItem) -> Unit,
    onReceiveForwardLongClicked: (ConversationViewItem.Receive.Forward) -> Unit,
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
        bindWithBinding {
            val forwardMessage = item.forwardMessage
            when (forwardMessage) {
                is ConversationTextItem -> {
                    forwardAuthorName.setPrintableText(item.forwardMessage.userName)
                    messageContent.setPrintableText(item.forwardMessage.content as PrintableText)
                    messageContent.addCommonLinks(onUserTagClicked)
                    forwardDocumentContent.isVisible = false
                    rvImages.isVisible = false
                    ivArrow.isVisible = true
                    messageContent.isVisible = true
                }
                is ConversationImageItem -> {
                    ivArrow.isVisible = false
                    messageContent.isVisible = false
                    rvImages.isVisible = true
                    createImageAdapter(
                        imageRecyclerView = rvImages,
                        items = forwardMessage.metaInfo,
                        onImageClicked = { imageMeta, pos ->
                            onImagesClicked(imageMeta, pos)
                        })
                    forwardDocumentContent.isVisible = false
                    forwardAuthorName.text = getString(
                        R.string.forward_image_from_ph,
                        getPrintableRawText(item.forwardMessage.userName)
                    )
                }
                is ConversationDocumentItem -> {
                    renderForward(
                        forwardDocumentContent = forwardDocumentContent,
                        forwardMessageName = forwardAuthorName,
                        forwardUserName = forwardMessage.userName,
                        forwardImages = rvImages,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationReceiveTextReplyImageAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onReceiveTextReplyImageLongClicked: (ConversationViewItem.Receive.TextReplyOnImage) -> Unit,
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,
    onReplyMessage: (ConversationViewItem) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                rvImages.isVisible = true
                createImageAdapter(
                    imageRecyclerView = rvImages,
                    items = it.metaInfo,
                    onImageClicked = { imageMeta, pos ->
                        onImagesClicked(imageMeta, pos)
                    })
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                binding.replyDocumentContent.isVisible = false

            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                renderReply(
                    replyDocumentContent = replyDocumentContent,
                    replyMessageName = replyMessageName,
                    replyUserName = it.userName,
                    replyImages = rvImages
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }


fun createConversationReceiveImageAdapterDelegate(
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReceiveImageLongClicked: (ConversationViewItem.Receive.Image) -> Unit,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Receive.Image, ConversationItemReceiveImageBinding>(
        ConversationItemReceiveImageBinding::inflate
    ) {
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
            createImageAdapter(
                imageRecyclerView = messageContent,
                items = item.metaInfo,
                onImageClicked = { imageMeta, pos ->
                    onImagesClicked(imageMeta, pos)
                })

            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationReceiveDocumentAdapterDelegate(
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit,
    onReceiveDocumentLongClicked: (ConversationViewItem.Receive) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }


//---Адаптеры для принятых групповых сообщений---//

fun createConversationGroupTextReplyImageAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onGroupTextReplyImageLongClicked: (ConversationViewItem.Group.TextReplyOnImage) -> Unit,
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,
    onReplyMessage: (ConversationViewItem) -> Unit,
    onProfileClicked: (ConversationViewItem.Group) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
        binding.avatar.onClick {
            onProfileClicked(item)
        }
        bindWithBinding {
            viewBinderHelper.bind(root, item.id.toString())
            viewBinderHelper.setOpenOnlyOne(true)

            (item.replyMessage as? ConversationImageItem)?.let {
                rvImages.isVisible = true
                createImageAdapter(
                    imageRecyclerView = rvImages,
                    items = it.metaInfo,
                    onImageClicked = { imageMeta, pos ->
                        onImagesClicked(imageMeta, pos)
                    })
                replyMessageName.text =
                    context.getString(R.string.reply_image_for_ph, getPrintableRawText(it.userName))
                binding.replyDocumentContent.isVisible = false


            }
            (item.replyMessage as? ConversationDocumentItem)?.let {
                renderReply(
                    replyDocumentContent = replyDocumentContent,
                    replyMessageName = replyMessageName,
                    replyUserName = it.userName,
                    replyImages = rvImages
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationGroupTextAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onGroupMessageLongClicked: (ConversationViewItem.Group.Text) -> Unit,
    onReplyMessage: (ConversationViewItem) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }


fun createConversationGroupForwardAdapterDelegate(
    onUserTagClicked: (String) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessage: (ConversationViewItem) -> Unit,
    onGroupForwardLongClicked: (ConversationViewItem.Group.Forward) -> Unit,
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
                    rvImages.isVisible = false
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
                    rvImages.isVisible = true
                    createImageAdapter(
                        imageRecyclerView = rvImages,
                        items = forwardMessage.metaInfo,
                        onImageClicked = { imageMeta, pos ->
                            onImagesClicked(imageMeta, pos)
                        })
                }
                is ConversationDocumentItem -> {
                    renderForward(
                        forwardDocumentContent = forwardDocumentContent,
                        forwardMessageName = forwardAuthorName,
                        forwardUserName = forwardMessage.userName,
                        forwardImages = rvImages,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationGroupImageAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onImagesClicked: (List<MetaInfo>, Int) -> Unit,
    viewBinderHelper: ViewBinderHelper,
    onReplyMessageImage: (item: ConversationViewItem) -> Unit,
    onGroupImageLongClicked: (ConversationViewItem.Group.Image) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
) =
    adapterDelegateViewBinding<ConversationViewItem.Group.Image, ConversationItemGroupImageBinding>(
        ConversationItemGroupImageBinding::inflate
    ) {
        binding.avatar.onClick {
            onGroupProfileItemClicked(item)
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
            createImageAdapter(
                imageRecyclerView = messageContent,
                items = item.metaInfo,
                onImageClicked = { imageMeta, pos ->
                    onImagesClicked(imageMeta, pos)
                })
            sendTimeView.setPrintableText(item.time)
            sendTimeView.isVisible = item.timeVisible
            avatar.loadCircle(url = item.avatar, placeholderRes = R.drawable.ic_avatar_placeholder)
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
        }
    }

fun createConversationGroupDocumentAdapterDelegate(
    onGroupProfileItemClicked: (ConversationViewItem.Group) -> Unit,
    onDownloadDocument: (meta: MetaInfo, progressListener: ProgressListener) -> Unit,
    onReplyMessageDocument: (item: ConversationViewItem) -> Unit,
    onGroupDocumentLongClicked: (ConversationViewItem.Group) -> Unit,
    onReactionClicked: (messageId: Long, reactionViewItem: ReactionsViewItem) -> Unit,
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
            createReactionsAdapter(listReactions, item.reactions) {
                onReactionClicked(item.id, it)
            }
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
    forwardImages: RecyclerView,
    forwardText: TextView,
) {
    forwardText.isVisible = false
    forwardImages.isVisible = false
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
    replyImages: RecyclerView,
) {
    replyImages.isVisible = false
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
    onImageClicked: (List<MetaInfo>, Int) -> Unit,
    items: List<MetaInfo>,
) {
    Log.d("ConversationAdapter", "createImageAdapter items: $items")
    val adapterImage = ConversationImageAdapter(
        onImageClick = { imageMeta ->
            onImageClicked(items, items.indexOf(imageMeta))
        }
    )
    imageRecyclerView.adapter = adapterImage
    val flexboxManager =
        FlexboxLayoutManager(imageRecyclerView.context, FlexDirection.ROW, FlexWrap.WRAP)

    imageRecyclerView.layoutManager = flexboxManager
    imageRecyclerView.itemAnimator = null
    imageRecyclerView.isNestedScrollingEnabled = false
    adapterImage.items = items.createAdapterItems()
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

private fun createReactionsAdapter(
    recyclerViewReactions: RecyclerView,
    items: List<ReactionsViewItem>,
    onReactionClicked: (reactionViewItem: ReactionsViewItem) -> Unit
) {
    val adapter = ReactionsAdapter(onItemClicked = onReactionClicked)
    recyclerViewReactions.layoutManager = FlexboxLayoutManager(recyclerViewReactions.context)
    recyclerViewReactions.adapter = adapter
    adapter.items = items
}

private fun SentStatus.canSwipe() = this != SentStatus.Error && this != SentStatus.Loading

private fun getMockReactions() = listOf(
    ReactionsViewItem("&#128522;", 3, listOf("", "", ""), R.color.blue2, true),
    ReactionsViewItem("&#129306;", 3, listOf("", "", ""), R.color.blue2, true),
    ReactionsViewItem("&#9995;", 53, listOf("", "", ""), R.color.blue2, true),
    ReactionsViewItem("&#128165;", 3, listOf("", "", ""), R.color.blue2, true),
    ReactionsViewItem("&#128164;", 3, listOf("", "", ""), R.color.blue2, true),
    ReactionsViewItem("&#9996;", 100, listOf(), R.color.blue2, true),
    ReactionsViewItem("&#128076;", 20, listOf(), R.color.blue2, true),
    ReactionsViewItem("&#128072;", 3, listOf("", "", ""), R.color.blue2, true),
    ReactionsViewItem("&#129305;", 2, listOf("", ""), R.color.blue2, true),
).sortedByDescending { it.userCount }