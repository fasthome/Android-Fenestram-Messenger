package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper

import android.util.Log
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.SendMessageResult
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getFuzzyDateString
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun List<Message>.toConversationItems(
    selfUserId: Long?,
    isGroup: Boolean,
): Map<String, ConversationViewItem> = this.associateBy({
    UUID.randomUUID().toString()
}, {
    it.toConversationViewItem(selfUserId, isGroup)
})

fun Message.toConversationViewItem(
    selfUserId: Long? = null,
    isGroup: Boolean? = null,
): ConversationViewItem {
    if (isSystem) {
        return ConversationViewItem.System(
            content = getFuzzyDateString(date),
            time = PrintableText.EMPTY,
            date = date,
            id = id,
            sentStatus = SentStatus.None
        )
    }
    return when (selfUserId) {
        userSenderId -> {
            ConversationViewItem.Self(
                content = PrintableText.Raw(text),
                time = PrintableText.Raw(timeFormatter.format(date)),
                sentStatus = SentStatus.Sent,
                date = date,
                id = id,
                localId = UUID.randomUUID().toString()
            )
        }
        else -> {
            if (isGroup == true) {
                ConversationViewItem.Group(
                    content = PrintableText.Raw(text),
                    time = PrintableText.Raw(timeFormatter.format(date)),
                    sentStatus = SentStatus.None,
                    userName = PrintableText.Raw(initiator?.name ?: ""),
                    avatar = initiator?.avatar ?: "",
                    date = date,
                    id = id
                )
            } else {
                ConversationViewItem.Receive(
                    content = PrintableText.Raw(text),
                    time = PrintableText.Raw(timeFormatter.format(date)),
                    sentStatus = SentStatus.None,
                    date = date,
                    id = id
                )
            }
        }
    }
}

/**
 * Алгоритм для расставления хедеров с датами.
 * Можно предложить другое решение, если таковое есть
 *
 * Работает только для перевернутого [LinearLayoutManager]
 */
fun List<ConversationViewItem>.addHeaders(): List<ConversationViewItem> {
    val messages = this.toMutableList()

    val messagesWithHeaders = mutableListOf<ConversationViewItem>()
    for (position in messages.indices) {
        val item = messages[position]
        if (position == 0) {
            messagesWithHeaders.add(item)
            if(messages.size == 1){
                messagesWithHeaders.add(createSystem(item.date ?: continue))
                continue
            }
            if (item.date?.dayOfMonth != messages[position + 1].date?.dayOfMonth) {
                messagesWithHeaders.add(createSystem(item.date ?: continue))
                continue
            }
            continue
        }
        if (position == messages.lastIndex) {
            messagesWithHeaders.add(item)
            messagesWithHeaders.add(createSystem(item.date ?: continue))
            continue
        }

        messagesWithHeaders.add(item)

        if (item.date?.dayOfMonth != messages[position + 1].date?.dayOfMonth) {
            messagesWithHeaders.add(createSystem(item.date ?: continue))
            continue
        }
    }

    return messagesWithHeaders
}

fun createMessage(text: String) = ConversationViewItem.Self(
    content = PrintableText.Raw(text),
    time = PrintableText.Raw(timeFormatter.format(ZonedDateTime.now())),
    sentStatus = SentStatus.Loading,
    date = ZonedDateTime.now(),
    id = 0,
    localId = UUID.randomUUID().toString()
)

fun createSystem(date : ZonedDateTime) = ConversationViewItem.System(
    content = getFuzzyDateString(date),
    time = PrintableText.EMPTY,
    date = date,
    id = 0,
    sentStatus = SentStatus.None
)