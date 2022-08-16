package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.mapper

import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun toMessengerViewItem(chat : Chat): MessengerViewItem? {
    return MessengerViewItem(
        id = chat.id ?: return null,
        avatar = 0,
        name = PrintableText.Raw(chat.name),
        newMessages = 0,
        lastMessage = chat.messages.lastOrNull()?.let { message ->
            PrintableText.Raw(message.text)
        } ?: PrintableText.EMPTY,
        time = PrintableText.Raw(dateFormatter.format(chat.time))
    )
}
