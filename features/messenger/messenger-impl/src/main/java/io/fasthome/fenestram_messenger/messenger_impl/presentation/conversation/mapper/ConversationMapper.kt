package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper

import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getFuzzyDateString
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun List<Message>.toConversationItems(
    selfUserId: Long,
    isGroup: Boolean,
    lastMessage: ConversationViewItem? = null,
    messagesEmpty: Boolean = false
): List<ConversationViewItem> {
    val systemMessageIndexes = mutableListOf<Pair<Int, ZonedDateTime?>>()

    val conversationItems = this.mapIndexedNotNull { index, message ->
        if (this.size > 1) {
            if (index == 0) {
                systemMessageIndexes.add(index to this[index].date)
                null
            } else {
                if (message.date?.dayOfMonth != (this[index - 1].date)?.dayOfMonth) {
                    systemMessageIndexes.add(index to message.date)
                }
                message.toConversationViewItem(selfUserId, isGroup)
            }
        } else {
            if (messagesEmpty) {
                systemMessageIndexes.add(index to message.date)
            } else {
                lastMessage?.let {
                    if (message.date?.dayOfMonth != lastMessage.date?.dayOfMonth) {
                        systemMessageIndexes.add(index to message.date)
                    }
                }
            }
            message.toConversationViewItem(selfUserId, isGroup)
        }
    }.toMutableList()

    systemMessageIndexes.forEach {
        conversationItems.add(
            it.first,
            Message.onlyDate(it.second ?: return@forEach)
                .toConversationViewItem(isSystemItem = true)
        )
    }
    return conversationItems
}

fun Message.toConversationViewItem(
    selfUserId: Long? = null,
    isGroup: Boolean? = null,
    isSystemItem: Boolean = false
): ConversationViewItem {
    if (isSystemItem) {
        return ConversationViewItem.System(
            content = getFuzzyDateString(date),
            time = PrintableText.EMPTY,
            date = date,
            id = id
        )
    }
    return when (selfUserId) {
        userSenderId -> {
            ConversationViewItem.Self(
                content = PrintableText.Raw(text),
                time = PrintableText.Raw(timeFormatter.format(date)),
                sendStatus = false,
                date = date,
                id = id
            )
        }
        else -> {
            if (isGroup == true) {
                ConversationViewItem.Group(
                    content = PrintableText.Raw(text),
                    time = PrintableText.Raw(timeFormatter.format(date)),
                    sendStatus = false,
                    userName = PrintableText.Raw(initiator?.name ?: ""),
                    avatar = initiator?.avatar ?: "",
                    date = date,
                    id = id
                )
            } else {
                ConversationViewItem.Receive(
                    content = PrintableText.Raw(text),
                    time = PrintableText.Raw(timeFormatter.format(date)),
                    sendStatus = false,
                    date = date,
                    id = id
                )
            }
        }
    }
}