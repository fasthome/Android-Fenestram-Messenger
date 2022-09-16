package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model

import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import java.time.ZonedDateTime

typealias OnStatusChanged = (SentStatus) -> Unit

sealed interface ConversationViewItem {

    val id: Long
    val content: PrintableText
    val time: PrintableText
    val date: ZonedDateTime?
    val sentStatus : SentStatus

    data class Self(
        override val id: Long,
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        override var sentStatus: SentStatus,
        val localId : String
    ) : ConversationViewItem {

        val statusIcon = getStatusIcon(sentStatus)
    }

    data class Receive(
        override val id: Long,
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        override val sentStatus: SentStatus,
    ) : ConversationViewItem

    data class Group(
        override val id: Long,
        override val content: PrintableText,
        override val time: PrintableText,
        override val date: ZonedDateTime?,
        override val sentStatus: SentStatus,
        val userName: PrintableText,
        val avatar: String
    ) : ConversationViewItem

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
        SentStatus.Error-> R.drawable.ic_status_message_error
        SentStatus.Read -> R.drawable.ic_status_message_read
        SentStatus.None -> 0
    }

enum class SentStatus {
    Sent, Error, Read, Loading, None
}