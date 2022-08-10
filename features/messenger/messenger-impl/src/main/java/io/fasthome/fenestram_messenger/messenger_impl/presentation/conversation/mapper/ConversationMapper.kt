package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper

import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.util.PrintableText

fun Message.toConversationViewItem(selfUserId: Long) =
    when (selfUserId) {
        userSenderId -> {
            ConversationViewItem.Self(
                content = PrintableText.Raw(text),
                time = createdAt,
                sendStatus = false
            )
        }
        else -> {
            ConversationViewItem.Receive(
                content = PrintableText.Raw(text),
                time = createdAt,
                sendStatus = false
            )
        }
    }