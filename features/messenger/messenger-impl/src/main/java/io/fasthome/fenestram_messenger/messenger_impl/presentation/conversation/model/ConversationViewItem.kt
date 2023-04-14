package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import android.graphics.drawable.Drawable
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.model.MetaInfo
import java.io.File
import java.time.ZonedDateTime

typealias OnStatusChanged = (SentStatus) -> Unit

data class ConversationSelfItemTheme(
    val background: Drawable,
)

data class ConversationReceiveItemTheme(
    val background: Drawable,
    val textColor: Int,
    val documentColor: Int,
)

data class ConversationGroupItemTheme(
    val background: Drawable,
    val textColor: Int,
    val documentColor: Int,
)


interface ConversationImageItem {
    val id: Long
    val userName: PrintableText
    val content: String
    val metaInfo: List<MetaInfo>
}

interface ConversationTextItem {
    val id: Long
    val userName: PrintableText
    val content: PrintableText
}

interface ConversationDocumentItem {
    val userName: PrintableText
    val metaInfo: List<MetaInfo>
}

sealed interface ConversationViewItem {

    val id: Long
    val content: Any
    val time: PrintableText
    val date: ZonedDateTime?
    val timeVisible: Boolean
    val sentStatus: SentStatus
    val nickname: String?
    var userName: PrintableText
    val messageType: String?
    val replyMessage: ConversationViewItem?
    val reactions: List<ReactionsViewItem>?

    val statusIcon: Int
        get() = getStatusIcon(sentStatus)

    sealed class Self : ConversationViewItem {
        abstract val localId: String
        abstract val metaInfo: List<MetaInfo>
        abstract val conversationSelfItemTheme: ConversationSelfItemTheme?

        data class Text(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val localId: String,
            override val timeVisible: Boolean,
            val isEdited: Boolean,
            override val nickname: String?,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            override val metaInfo: List<MetaInfo> = emptyList(),
            override val conversationSelfItemTheme: ConversationSelfItemTheme? = null,
        ) : Self(), ConversationTextItem

        data class TextReplyOnImage(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val localId: String,
            override val timeVisible: Boolean,
            val isEdited: Boolean,
            override val nickname: String?,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            override val metaInfo: List<MetaInfo> = emptyList(),
            override val conversationSelfItemTheme: ConversationSelfItemTheme? = null,
        ) : Self(), ConversationTextItem

        data class Forward(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val localId: String,
            override val timeVisible: Boolean,
            override val nickname: String?,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            val forwardMessage: ConversationViewItem,
            override val metaInfo: List<MetaInfo> = emptyList(),
            override val conversationSelfItemTheme: ConversationSelfItemTheme? = null,
        ) : Self()

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val localId: String,
            override val timeVisible: Boolean,
            val loadableContent: List<Content>? = null,
            override val nickname: String?,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            override val metaInfo: List<MetaInfo> = emptyList(),
            override val conversationSelfItemTheme: ConversationSelfItemTheme? = null,
        ) : Self(), ConversationImageItem

        data class Document(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val localId: String,
            override val timeVisible: Boolean,
            val files: List<File>?,
            val path: List<String>?,
            override val nickname: String? = null,
            override val messageType: String? = null,
            override val replyMessage: ConversationViewItem? = null,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            override val metaInfo: List<MetaInfo>,
            override val conversationSelfItemTheme: ConversationSelfItemTheme? = null,
        ) : Self(), ConversationDocumentItem
    }

    sealed class Receive : ConversationViewItem {

        abstract val conversationReceiveItemTheme: ConversationReceiveItemTheme?

        data class Text(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val timeVisible: Boolean,
            val isEdited: Boolean,
            override val nickname: String?,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            override val conversationReceiveItemTheme: ConversationReceiveItemTheme? = null,
        ) : Receive(), ConversationTextItem

        data class Forward(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val timeVisible: Boolean,
            override val nickname: String?,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            val forwardMessage: ConversationViewItem,
            override val conversationReceiveItemTheme: ConversationReceiveItemTheme? = null,
        ) : Receive()

        data class TextReplyOnImage(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val timeVisible: Boolean,
            val isEdited: Boolean,
            override val nickname: String?,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            override val conversationReceiveItemTheme: ConversationReceiveItemTheme? = null,
        ) : Receive(), ConversationTextItem

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val timeVisible: Boolean,
            override val nickname: String?,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            override val metaInfo: List<MetaInfo>,
            override val conversationReceiveItemTheme: ConversationReceiveItemTheme? = null,
        ) : Receive(), ConversationImageItem

        data class Document(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val timeVisible: Boolean,
            var path: String? = null,
            override val nickname: String? = null,
            override val messageType: String? = null,
            override val replyMessage: ConversationViewItem? = null,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            override val metaInfo: List<MetaInfo>,
            override val conversationReceiveItemTheme: ConversationReceiveItemTheme? = null,
        ) : Receive(), ConversationDocumentItem
    }

    sealed class Group(
        override var userName: PrintableText,
        open val avatar: String,
        open val phone: String,
        open val userId: Long,
        open val conversationGroupItemTheme: ConversationGroupItemTheme?,
    ) : ConversationViewItem {
        data class Text(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override var userName: PrintableText,
            override val avatar: String,
            override val phone: String,
            override val nickname: String,
            override val userId: Long,
            override val timeVisible: Boolean,
            val isEdited: Boolean,
            override val messageType: String?,
            override val reactions: List<ReactionsViewItem>,
            override val replyMessage: ConversationViewItem?,
            override val conversationGroupItemTheme: ConversationGroupItemTheme?,
        ) : Group(userName, avatar, phone, userId, conversationGroupItemTheme), ConversationTextItem

        data class Forward(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val timeVisible: Boolean,
            override val avatar: String,
            override val phone: String,
            override val nickname: String?,
            override val userId: Long,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override var userName: PrintableText,
            override val reactions: List<ReactionsViewItem>,
            val forwardMessage: ConversationViewItem,
            override val conversationGroupItemTheme: ConversationGroupItemTheme?,
        ) : Group(userName, avatar, phone, userId, conversationGroupItemTheme)

        data class TextReplyOnImage(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override var userName: PrintableText,
            override val avatar: String,
            override val phone: String,
            override val nickname: String,
            override val userId: Long,
            override val timeVisible: Boolean,
            val isEdited: Boolean,
            override val messageType: String?,
            override val reactions: List<ReactionsViewItem>,
            override val replyMessage: ConversationViewItem?,
            override val conversationGroupItemTheme: ConversationGroupItemTheme?,
        ) : Group(userName, avatar, phone, userId, conversationGroupItemTheme), ConversationTextItem

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override var userName: PrintableText,
            override val avatar: String,
            override val phone: String,
            override val nickname: String,
            override val userId: Long,
            override val timeVisible: Boolean,
            override val messageType: String?,
            override val replyMessage: ConversationViewItem?,
            override val reactions: List<ReactionsViewItem>,
            override val metaInfo: List<MetaInfo>,
            override val conversationGroupItemTheme: ConversationGroupItemTheme?,
        ) : Group(userName, avatar, phone, userId, conversationGroupItemTheme),
            ConversationImageItem

        data class Document(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override var userName: PrintableText,
            override val avatar: String,
            override val phone: String,
            override val timeVisible: Boolean,
            override val nickname: String,
            override val userId: Long,
            var path: String? = null,
            override val messageType: String? = null,
            override val replyMessage: ConversationViewItem? = null,
            override val reactions: List<ReactionsViewItem>,
            override val metaInfo: List<MetaInfo> = emptyList(),
            override val conversationGroupItemTheme: ConversationGroupItemTheme?,
        ) : Group(userName, avatar, phone, userId, conversationGroupItemTheme),
            ConversationDocumentItem
    }

    data class System(
        override val id: Long,
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        override val sentStatus: SentStatus,
        override var timeVisible: Boolean,
        override val nickname: String? = null,
        override val messageType: String? = null,
        override val replyMessage: ConversationViewItem? = null,
        override val reactions: List<ReactionsViewItem>? = null,
        override var userName: PrintableText = PrintableText.EMPTY,
    ) : ConversationViewItem

}

fun ConversationViewItem.canDelete(): Boolean {
    return this is ConversationViewItem.Self
}

fun getStatusIcon(sentStatus: SentStatus) =
    when (sentStatus) {
        SentStatus.Loading -> R.drawable.ic_status_message_load
        SentStatus.Sent -> R.drawable.ic_status_message_sent
        SentStatus.Error -> R.drawable.ic_status_message_error
        SentStatus.Read -> R.drawable.ic_status_message_read
        SentStatus.Received -> R.drawable.ic_status_message_received
        SentStatus.None -> 0
    }

enum class SentStatus {
    Sent, Error, Read, Loading, Received, None
}