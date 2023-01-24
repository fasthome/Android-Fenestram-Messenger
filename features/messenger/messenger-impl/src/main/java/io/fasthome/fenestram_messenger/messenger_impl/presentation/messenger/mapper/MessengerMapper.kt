package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.mapper

import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessageAction
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper.*
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.MetaInfo
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.getStatusIcon
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.LastMessage
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getFuzzyDateString
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("HH:mm")

class MessengerMapper(private val profileImageUrlConverter: StorageUrlConverter) {

    var messageActions = mutableListOf<MessageAction>()

    fun toMessengerViewItem(chat: Chat): MessengerViewItem {
        val lastMessage = messageActions.find { chat.id == it.chatId }?.let {
            LastMessage.UserStatus(
                it.userStatus.toPrintableText(
                    it.userName,
                    chat.isGroup,
                    null
                )
            )
        } ?: chat.messages.lastOrNull()?.let { message ->
            when (message.messageType) {
                MESSAGE_TYPE_TEXT -> {
                    LastMessage.Text(PrintableText.Raw(message.text))
                }
                MESSAGE_TYPE_SYSTEM -> {
                    LastMessage.Text(PrintableText.Raw(message.text))
                }
                MESSAGE_TYPE_IMAGE -> {
                    LastMessage.Image(imageUrl = message.content.map { MetaInfo(it) })
                }
                MESSAGE_TYPE_DOCUMENT -> {
                    LastMessage.Document
                }
                else -> {
                    LastMessage.Text(PrintableText.EMPTY)
                }
            }
        } ?: LastMessage.Text(PrintableText.EMPTY)

        val sentStatus = if (chat.pendingMessages == 0L) {
            if (chat.messages[0].usersHaveRead.isNullOrEmpty()) SentStatus.Received else SentStatus.Read
        } else {
            SentStatus.None
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
            isGroup = chat.isGroup,
            sentStatus = sentStatus,
            statusIcon = if (sentStatus != SentStatus.None) {
                getStatusIcon(sentStatus)
            } else {
                R.drawable.bg_not_read
            },
            pendingAmount = if (chat.pendingMessages == 0L) PrintableText.EMPTY
            else PrintableText.Raw(chat.pendingMessages.toString())
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