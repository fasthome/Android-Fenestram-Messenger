package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.PrintableText
import java.time.ZonedDateTime

typealias OnStatusChanged = (SentStatus) -> Unit

interface ConversationImageItem {
    val content: String
}
interface ConversationTextItem {
    val content: PrintableText
}

sealed interface ConversationViewItem {

    val id: Long
    val content: Any
    val time: PrintableText
    val date: ZonedDateTime?
    val timeVisible : Boolean
    val sentStatus: SentStatus
    val nickname: String?

    val statusIcon: Int
        get() = getStatusIcon(sentStatus)

    sealed class Self : ConversationViewItem {
        abstract val localId: String

        data class Text(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val localId: String,
            override val timeVisible: Boolean,
            val isEdited: Boolean,
            override val nickname: String?
        ) : Self(), ConversationTextItem

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val localId: String,
            override val timeVisible: Boolean,
            val loadableContent: Content? = null,
            override val nickname: String?
        ) : Self(), ConversationImageItem
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
            override val nickname: String?
        ) : Receive(), ConversationTextItem

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val timeVisible: Boolean,
            override val nickname: String?
        ) : Receive(), ConversationImageItem
    }

    sealed class Group(
        open val userName: PrintableText,
        open val avatar: String,
        open val phone: String,
        open val userId: Long
    ) : ConversationViewItem {
        data class Text(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val userName: PrintableText,
            override val avatar: String,
            override val phone: String,
            override val nickname: String?,
            override val userId: Long,
            override val timeVisible: Boolean,
            val isEdited: Boolean
        ) : Group(userName, avatar, phone, userId), ConversationTextItem

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val userName: PrintableText,
            override val avatar: String,
            override val phone: String,
            override val nickname: String?,
            override val userId: Long,
            override val timeVisible: Boolean,
        ) : Group(userName, avatar, phone, userId), ConversationImageItem
    }

    data class System(
        override val id: Long,
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        override val sentStatus: SentStatus,
        override var timeVisible: Boolean,
        override val nickname: String? = null
    ) : ConversationViewItem

}

fun getStatusIcon(sentStatus: SentStatus) =
    when (sentStatus) {
        SentStatus.Loading -> R.drawable.ic_status_message_load
        SentStatus.Sent -> R.drawable.ic_status_message_sent
        SentStatus.Error -> R.drawable.ic_status_message_error
        SentStatus.Read -> R.drawable.ic_status_message_read
        SentStatus.None -> 0
    }

enum class SentStatus {
    Sent, Error, Read, Loading, None
}