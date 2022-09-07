package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper

import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun Message.toConversationViewItem(selfUserId: Long, isGroup: Boolean) =
    when (selfUserId) {
        userSenderId -> {
            ConversationViewItem.Self(
                content = PrintableText.Raw(text),
                time = PrintableText.Raw(dateFormatter.format(date)),
                sendStatus = false
            )
        }
        else -> {
            if (isGroup) {
                ConversationViewItem.Group(
                    content = PrintableText.Raw(text),
                    time = PrintableText.Raw(dateFormatter.format(date)),
                    sendStatus = false,
                    userName = PrintableText.Raw(initiator?.name ?: ""),
                    avatar = initiator?.avatar ?: ""
                )
            } else {
                ConversationViewItem.Receive(
                    content = PrintableText.Raw(text),
                    time = PrintableText.Raw(dateFormatter.format(date)),
                    sendStatus = false
                )
            }
        }
    }