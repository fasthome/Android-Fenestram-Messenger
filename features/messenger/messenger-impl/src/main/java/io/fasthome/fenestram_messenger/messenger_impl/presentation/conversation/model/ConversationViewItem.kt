package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.data.service.model.ContentResponse
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.fileSizeInMb
import io.fasthome.fenestram_messenger.util.getPrettySize
import java.io.File
import java.time.ZonedDateTime

typealias OnStatusChanged = (SentStatus) -> Unit

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

    val statusIcon: Int
        get() = getStatusIcon(sentStatus)

    sealed class Self : ConversationViewItem {
        abstract val localId: String
        abstract val metaInfo: List<MetaInfo>

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
            override val metaInfo: List<MetaInfo> = emptyList(),
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
            override val metaInfo: List<MetaInfo> = emptyList(),
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
            val forwardMessage: ConversationViewItem,
            override val metaInfo: List<MetaInfo> = emptyList(),
        ) : Self()

        data class  Image(
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
            override val metaInfo: List<MetaInfo> = emptyList(),
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
            override val metaInfo: List<MetaInfo>
        ) : Self(), ConversationDocumentItem
    }

    sealed class Receive : ConversationViewItem {
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
            val forwardMessage: ConversationViewItem
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
            override val metaInfo: List<MetaInfo>,
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
            override val metaInfo: List<MetaInfo>
        ) : Receive(), ConversationDocumentItem
    }

    sealed class Group(
        override var userName: PrintableText,
        open val avatar: String,
        open val phone: String,
        open val userId: Long,
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
            override val replyMessage: ConversationViewItem?,
        ) : Group(userName, avatar, phone, userId), ConversationTextItem

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
            val forwardMessage: ConversationViewItem
        ) : Group(userName, avatar, phone, userId)

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
            override val replyMessage: ConversationViewItem?,
        ) : Group(userName, avatar, phone, userId), ConversationTextItem

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
            override val metaInfo: List<MetaInfo>,
        ) : Group(userName, avatar, phone, userId), ConversationImageItem

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
            override val metaInfo: List<MetaInfo> = emptyList()
        ) : Group(userName, avatar, phone, userId), ConversationDocumentItem
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

data class MetaInfo(
    var name: PrintableText,
    /**
     * Расширение файла, напр. ".txt"
     */
    var extension: String,
    /**
     * Размер файла указанный в мегабайтах
     */
    var size: Float,
    var url: String?
) {
    constructor() : this(PrintableText.Raw("File"), "", 0f, "")
    constructor(content: ContentResponse) : this(
        PrintableText.Raw(content.name),
        content.extension,
        content.size,
        content.url
    )
    constructor(file: File) : this(
        PrintableText.Raw(file.name),
        file.extension,
        fileSizeInMb(file.length()),
        null
    )
}