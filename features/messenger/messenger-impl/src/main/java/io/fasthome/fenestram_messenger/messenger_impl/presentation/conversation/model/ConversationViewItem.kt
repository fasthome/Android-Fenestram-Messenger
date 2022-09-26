package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import android.graphics.Bitmap
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import java.io.File
import java.time.ZonedDateTime

typealias OnStatusChanged = (SentStatus) -> Unit

sealed interface ConversationViewItem {

    val id: Long
    val content: Any
    val time: PrintableText
    val date: ZonedDateTime?
    val sentStatus: SentStatus

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
        ) : Self()

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override var sentStatus: SentStatus,
            override val localId: String,
            val bitmap: Bitmap? = null,
            val file: File? = null
        ) : Self()
    }

    sealed class Receive : ConversationViewItem {
        data class Text(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
        ) : Receive()

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
        ) : Receive()
    }

    sealed class Group(
        open val userName: PrintableText,
        open val avatar: String,
        open val phone : String
    ) : ConversationViewItem {
        data class Text(
            override val id: Long,
            override val content: PrintableText,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val userName: PrintableText,
            override val avatar: String,
            override val phone: String
        ) : Group(userName, avatar, phone)

        data class Image(
            override val id: Long,
            override val content: String,
            override val time: PrintableText,
            override val date: ZonedDateTime?,
            override val sentStatus: SentStatus,
            override val userName: PrintableText,
            override val avatar: String,
            override val phone: String
        ) : Group(userName, avatar, phone)
    }

    data class System(
        override val id: Long,
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        override val sentStatus: SentStatus,
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