package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.mapper

import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessageAction
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.MESSAGE_TYPE_IMAGE
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.MESSAGE_TYPE_SYSTEM
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.MESSAGE_TYPE_TEXT
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.toPrintableText
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.LastMessage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getFuzzyDateString
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("HH:mm")

class MessengerMapper(private val profileImageUrlConverter: ProfileImageUrlConverter) {

    var messageAction: MessageAction? = null

    fun toMessengerViewItem(chat: Chat): MessengerViewItem {
        val lastMessage = if (messageAction != null && chat.id == messageAction!!.chatId) {
            LastMessage.Status(
                messageAction!!.userStatus.toPrintableText(
                    messageAction!!.userName,
                    chat.isGroup
                )
            )
        } else {
            chat.messages.lastOrNull()?.let { message ->
                when (message.messageType) {
                    MESSAGE_TYPE_TEXT -> {
                        LastMessage.Text(PrintableText.Raw(message.text))
                    }
                    MESSAGE_TYPE_SYSTEM -> {
                        LastMessage.Text(PrintableText.Raw(message.text))
                    }
                    MESSAGE_TYPE_IMAGE -> {
                        LastMessage.Image(imageUrl = message.text)
                    }
                    else -> {
                        LastMessage.Text(PrintableText.EMPTY)
                    }
                }
            } ?: LastMessage.Text(PrintableText.EMPTY)
        }

        return MessengerViewItem(
            id = chat.id ?: 0,
            avatar = 0,
            name = PrintableText.Raw(chat.name),
            newMessages = 0,
            lastMessage = lastMessage,
            time = getTimeOrFuzzyDate(chat.time),
            profileImageUrl = profileImageUrlConverter.convert(chat.avatar),
            originalChat = chat,
            isGroup = chat.isGroup
        )
    }

    private fun getTimeOrFuzzyDate(datetime: ZonedDateTime?): PrintableText {
        return if (datetime?.dayOfMonth == ZonedDateTime.now().dayOfMonth) {
            PrintableText.Raw(dateFormatter.format(datetime))
        } else {
            getFuzzyDateString(datetime)
        }
    }
}