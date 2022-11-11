package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessageStatus
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.UserStatus
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.ConversationViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.uikit.image_view.glide_custom_loader.model.Content
import io.fasthome.fenestram_messenger.util.*
import io.ktor.util.reflect.*
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

const val MESSAGE_TYPE_TEXT = "text"
const val MESSAGE_TYPE_SYSTEM = "system"
const val MESSAGE_TYPE_IMAGE = "image"
const val MESSAGE_TYPE_DOCUMENT = "document"

const val MESSAGE_STATUS_SENT = "sent"
const val MESSAGE_STATUS_RECEIVED = "pending"
const val MESSAGE_STATUS_READ = "read"

fun List<Message>.toConversationItems(
    selfUserId: Long?,
    isGroup: Boolean,
    profileImageUrlConverter: StorageUrlConverter,
): Map<String, ConversationViewItem> = this.associateBy({
    UUID.randomUUID().toString()
}, {
    it.toConversationViewItem(selfUserId, isGroup, profileImageUrlConverter)
})

fun Message.toConversationViewItem(
    selfUserId: Long? = null,
    isGroup: Boolean? = null,
    profileImageUrlConverter: StorageUrlConverter,
): ConversationViewItem {
    if (isDate) {
        return ConversationViewItem.System(
            content = getFuzzyDateString(date),
            time = PrintableText.EMPTY,
            date = date,
            id = id,
            sentStatus = SentStatus.None,
            timeVisible = true
        )
    }
    val sentStatus =
        if (userSenderId == selfUserId && usersHaveRead!!.isNotEmpty()) SentStatus.Read else SentStatus.Received
    return when (selfUserId) {
        userSenderId -> {
            when (messageType) {
                MESSAGE_TYPE_TEXT -> {
                    if (replyMessage?.messageType == MESSAGE_TYPE_TEXT || replyMessage == null)
                        return ConversationViewItem.Self.Text(
                            content = PrintableText.Raw(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = sentStatus,
                            date = date,
                            id = id,
                            localId = UUID.randomUUID().toString(),
                            timeVisible = true,
                            isEdited = isEdited,
                            nickname = initiator?.nickname,
                            messageType = messageType,
                            replyMessage = replyMessage?.toConversationViewItem(
                                selfUserId,
                                isGroup,
                                profileImageUrlConverter
                            ),
                            userName = PrintableText.Raw(getName(initiator))
                        )
                    else
                        return ConversationViewItem.Self.TextReplyOnImage(
                            content = PrintableText.Raw(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = sentStatus,
                            date = date,
                            id = id,
                            localId = UUID.randomUUID().toString(),
                            timeVisible = true,
                            isEdited = isEdited,
                            nickname = initiator?.nickname,
                            messageType = messageType,
                            replyMessage = replyMessage.toConversationViewItem(
                                selfUserId,
                                isGroup,
                                profileImageUrlConverter
                            ),
                            userName = PrintableText.Raw(getName(initiator))
                        )
                }
                MESSAGE_TYPE_IMAGE -> {
                    ConversationViewItem.Self.Image(
                        content = profileImageUrlConverter.convert(text),
                        time = PrintableText.Raw(timeFormatter.format(date)),
                        sentStatus = sentStatus,
                        date = date,
                        id = id,
                        localId = UUID.randomUUID().toString(),
                        timeVisible = true,
                        nickname = initiator?.nickname,
                        messageType = messageType,
                        replyMessage = replyMessage?.toConversationViewItem(
                            selfUserId,
                            isGroup,
                            profileImageUrlConverter
                        ),
                        userName = PrintableText.Raw(getName(initiator))
                    )
                }

                MESSAGE_TYPE_DOCUMENT -> {
                    ConversationViewItem.Self.Document(
                        content = profileImageUrlConverter.convert(text),
                        time = PrintableText.Raw(timeFormatter.format(date)),
                        sentStatus = sentStatus,
                        date = date,
                        id = id,
                        localId = UUID.randomUUID().toString(),
                        timeVisible = true,
                        file = null,
                        path = null,
                        userName = PrintableText.Raw(getName(initiator))
                    )
                }

                MESSAGE_TYPE_SYSTEM -> {
                    ConversationViewItem.System(
                        content = PrintableText.Raw(text),
                        time = PrintableText.Raw(timeFormatter.format(date)),
                        sentStatus = sentStatus,
                        date = date,
                        id = id,
                        timeVisible = true,
                    )
                }
                else -> error("Unknown Message Type! type $messageType")
            }
        }
        else -> {
            if (isGroup == true) {
                when (messageType) {
                    MESSAGE_TYPE_TEXT -> {
                        if (replyMessage?.messageType == MESSAGE_TYPE_TEXT || replyMessage == null) {
                            ConversationViewItem.Group.Text(
                                content = PrintableText.Raw(text),
                                time = PrintableText.Raw(timeFormatter.format(date)),
                                sentStatus = SentStatus.None,
                                userName = PrintableText.Raw(getName(initiator)),
                                avatar = initiator?.avatar ?: "",
                                date = date,
                                id = id,
                                phone = initiator?.phone ?: "",
                                nickname = initiator?.nickname ?: "",
                                userId = initiator?.id ?: 0,
                                isEdited = isEdited,
                                timeVisible = true,
                                messageType = messageType,
                                replyMessage = replyMessage?.toConversationViewItem(
                                    selfUserId,
                                    isGroup,
                                    profileImageUrlConverter
                                )
                            )
                        } else {
                            ConversationViewItem.Group.TextReplyOnImage(
                                content = PrintableText.Raw(text),
                                time = PrintableText.Raw(timeFormatter.format(date)),
                                sentStatus = SentStatus.None,
                                userName = PrintableText.Raw(getName(initiator)),
                                avatar = initiator?.avatar ?: "",
                                date = date,
                                id = id,
                                phone = initiator?.phone ?: "",
                                nickname = initiator?.nickname ?: "",
                                userId = initiator?.id ?: 0,
                                isEdited = isEdited,
                                timeVisible = true,
                                messageType = messageType,
                                replyMessage = replyMessage.toConversationViewItem(
                                    selfUserId,
                                    isGroup,
                                    profileImageUrlConverter
                                )
                            )
                        }
                    }
                    MESSAGE_TYPE_IMAGE -> {
                        ConversationViewItem.Group.Image(
                            content = profileImageUrlConverter.convert(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = SentStatus.None,
                            userName = PrintableText.Raw(getName(initiator)),
                            avatar = initiator?.avatar ?: "",
                            date = date,
                            id = id,
                            phone = initiator?.phone ?: "",
                            nickname = initiator?.nickname ?: "",
                            userId = initiator?.id ?: 0,
                            timeVisible = true,
                            messageType = messageType,
                            replyMessage = replyMessage?.toConversationViewItem(
                                selfUserId,
                                isGroup,
                                profileImageUrlConverter
                            )
                        )
                    }

                    MESSAGE_TYPE_DOCUMENT -> {
                        ConversationViewItem.Group.Document(
                            content = profileImageUrlConverter.convert(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = SentStatus.None,
                            userName = PrintableText.Raw(getName(initiator)),
                            avatar = initiator?.avatar ?: "",
                            date = date,
                            id = id,
                            phone = initiator?.phone ?: "",
                            timeVisible = true,
                            nickname = initiator?.nickname ?: "",
                            userId = initiator?.id ?: 0,
                        )
                    }

                    MESSAGE_TYPE_SYSTEM -> {
                        ConversationViewItem.System(
                            content = PrintableText.Raw(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = SentStatus.Sent,
                            date = date,
                            id = id,
                            timeVisible = true
                        )
                    }
                    else -> error("Unknown Message Type! type $messageType")
                }
            } else {
                when (messageType) {
                    MESSAGE_TYPE_TEXT -> {
                        if (replyMessage?.messageType == MESSAGE_TYPE_TEXT || replyMessage == null)
                            return ConversationViewItem.Receive.Text(
                                content = PrintableText.Raw(text),
                                time = PrintableText.Raw(timeFormatter.format(date)),
                                sentStatus = SentStatus.None,
                                date = date,
                                id = id,
                                timeVisible = true,
                                isEdited = isEdited,
                                nickname = initiator?.nickname,
                                messageType = messageType,
                                replyMessage = replyMessage?.toConversationViewItem(
                                    selfUserId,
                                    isGroup,
                                    profileImageUrlConverter
                                ),
                                userName = PrintableText.Raw(getName(initiator))
                            )
                        else return ConversationViewItem.Receive.TextReplyOnImage(
                            content = PrintableText.Raw(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = SentStatus.None,
                            date = date,
                            id = id,
                            timeVisible = true,
                            isEdited = isEdited,
                            nickname = initiator?.nickname,
                            messageType = messageType,
                            replyMessage = replyMessage.toConversationViewItem(
                                selfUserId,
                                isGroup,
                                profileImageUrlConverter
                            ),
                            userName = PrintableText.Raw(getName(initiator))
                        )
                    }
                    MESSAGE_TYPE_IMAGE -> {
                        ConversationViewItem.Receive.Image(
                            content = profileImageUrlConverter.convert(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = SentStatus.None,
                            date = date,
                            id = id,
                            timeVisible = true,
                            nickname = initiator?.nickname,
                            messageType = messageType,
                            replyMessage = replyMessage?.toConversationViewItem(
                                selfUserId,
                                isGroup,
                                profileImageUrlConverter
                            ),
                            userName = PrintableText.Raw(getName(initiator))
                        )
                    }

                    MESSAGE_TYPE_DOCUMENT -> {
                        ConversationViewItem.Receive.Document(
                            content = profileImageUrlConverter.convert(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = SentStatus.Sent,
                            date = date,
                            id = id,
                            timeVisible = true,
                            userName = PrintableText.Raw(getName(initiator))
                        )
                    }

                    MESSAGE_TYPE_SYSTEM -> {
                        ConversationViewItem.System(
                            content = PrintableText.Raw(text),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = SentStatus.Sent,
                            date = date,
                            id = id,
                            timeVisible = true
                        )
                    }
                    else -> error("Unknown Message Type! type $messageType")
                }
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
            if (messages.size == 1) {
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

fun List<ConversationViewItem>.singleSameTime(): List<ConversationViewItem> {
    val messages = this.toMutableList()
    for (position in messages.indices) {
        if (position == this.lastIndex) continue
        val conversationViewItem = messages[position]
        val previousViewItem = messages[position + 1]

        if (!singleSameTimeIsContinue(conversationViewItem, previousViewItem)) {
            if (previousViewItem is ConversationViewItem.Group && previousViewItem.userId != (conversationViewItem as ConversationViewItem.Group).userId) {
                continue
            }

            var tempPreviousCounter = position + 1
            var nextInvisibleItem: ConversationViewItem

            var isInvisible = true

            while (isInvisible) {
                if (tempPreviousCounter >= messages.lastIndex) break
                nextInvisibleItem = messages[tempPreviousCounter]

                if (singleSameTimeIsContinue(conversationViewItem, nextInvisibleItem)) break

                isInvisible = conversationViewItem.date?.minute == nextInvisibleItem.date?.minute

                when (val next = messages[tempPreviousCounter]) {
                    is ConversationViewItem.System -> messages[tempPreviousCounter]
                    is ConversationViewItem.Group.Image -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Group.Text -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Receive.Image -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Receive.Text -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Self.Image -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Self.Text -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Group.Document -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Receive.Document -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Self.Document -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Group.TextReplyOnImage -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Receive.TextReplyOnImage -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                    is ConversationViewItem.Self.TextReplyOnImage -> messages[tempPreviousCounter] =
                        next.copy(timeVisible = !isInvisible)
                }

                tempPreviousCounter++
            }
        }
    }
    return messages
}

private fun singleSameTimeIsContinue(
    conversationViewItem: ConversationViewItem,
    otherConversationViewItem: ConversationViewItem,
): Boolean {
    return when {
        conversationViewItem is ConversationViewItem.Self && otherConversationViewItem is ConversationViewItem.Self -> false
        conversationViewItem is ConversationViewItem.Group && otherConversationViewItem is ConversationViewItem.Group -> false
        conversationViewItem is ConversationViewItem.Receive && otherConversationViewItem is ConversationViewItem.Receive -> false
        conversationViewItem is ConversationViewItem.System && otherConversationViewItem is ConversationViewItem.System -> false
        else -> true
    }
}

fun createTextMessage(text: String) = ConversationViewItem.Self.Text(
    content = PrintableText.Raw(text),
    time = PrintableText.Raw(timeFormatter.format(ZonedDateTime.now())),
    sentStatus = SentStatus.Loading,
    date = ZonedDateTime.now(),
    id = 0,
    localId = UUID.randomUUID().toString(),
    isEdited = false,
    timeVisible = true,
    nickname = null,
    messageType = "text",
    replyMessage = null,
    userName = PrintableText.EMPTY
)

fun createImageMessage(image: String?, loadableContent: Content, userName: String?) =
    ConversationViewItem.Self.Image(
        content = image ?: "",
        time = PrintableText.Raw(timeFormatter.format(ZonedDateTime.now())),
        sentStatus = SentStatus.Loading,
        date = ZonedDateTime.now(),
        id = 0,
        localId = UUID.randomUUID().toString(),
        loadableContent = loadableContent,
        timeVisible = true,
        nickname = userName,
        messageType = "image",
        replyMessage = null,
        userName = PrintableText.Raw(userName ?: "")
    )

fun createDocumentMessage(document: String?, file: File) = ConversationViewItem.Self.Document(
    content = document ?: run {
        //TODO костыль!!! Пока бэк не поддерживает все форматы файлов
        if (file.extension.equals("pdf", ignoreCase = true)) {
            file.extension
        } else {
            "TXT"
        }
    },
    time = PrintableText.Raw(timeFormatter.format(ZonedDateTime.now())),
    sentStatus = SentStatus.Loading,
    date = ZonedDateTime.now(),
    id = 0,
    localId = UUID.randomUUID().toString(),
    file = file,
    timeVisible = true,
    path = null,
    userName = PrintableText.EMPTY
)

fun createSystem(date: ZonedDateTime) = ConversationViewItem.System(
    content = getFuzzyDateString(date),
    time = PrintableText.EMPTY,
    date = date,
    id = 0,
    sentStatus = SentStatus.None,
    timeVisible = true
)

private fun getName(user: User?): String {
    if (user == null) return "Неизвестный пользователь"
    return when {
        user.contactName?.isNotEmpty() == true -> user.contactName!!
        user.name.isNotEmpty() -> user.name
        else -> user.phone.setMaskByCountry(Country.RUSSIA)
    }
}

fun UserStatus.toPrintableText(userName: String, isGroup: Boolean): PrintableText {
    return when (this) {
        UserStatus.Offline -> PrintableText.StringResource(R.string.user_status_offline)
        UserStatus.Online -> PrintableText.StringResource(R.string.user_status_online)
        UserStatus.Typing -> {
            if (isGroup)
                PrintableText.StringResource(R.string.group_user_status_typing, userName)
            else
                PrintableText.StringResource(R.string.user_status_typing)
        }
    }
}

fun getSentStatus(messageStatus: String): SentStatus {
    return when (messageStatus) {
        MESSAGE_STATUS_SENT -> SentStatus.Sent
        MESSAGE_STATUS_RECEIVED -> SentStatus.Received
        MESSAGE_STATUS_READ -> SentStatus.Read
        else -> SentStatus.None
    }
}

fun MessageStatus.toConversationViewItem(oldViewItem: ConversationViewItem): ConversationViewItem {
    return when (messageType) {
        MESSAGE_TYPE_TEXT -> {
            return when {
                (oldViewItem as? ConversationViewItem.Self.Text) != null -> oldViewItem.copy(
                    sentStatus = getSentStatus(
                        messageStatus
                    )
                )
                (oldViewItem as? ConversationViewItem.Self.TextReplyOnImage) != null -> oldViewItem.copy(
                    sentStatus = getSentStatus(
                        messageStatus
                    )
                )
                else -> error("Unknown View Item $oldViewItem !")
            }
        }
        MESSAGE_TYPE_IMAGE -> {
            (oldViewItem as ConversationViewItem.Self.Image).copy(
                sentStatus = getSentStatus(
                    messageStatus
                )
            )
        }

        MESSAGE_TYPE_DOCUMENT -> {
            (oldViewItem as ConversationViewItem.Self.Document).copy(
                sentStatus = getSentStatus(
                    messageStatus
                )
            )
        }
        MESSAGE_TYPE_SYSTEM -> {
            (oldViewItem as ConversationViewItem.System).copy(
                sentStatus = getSentStatus(
                    messageStatus
                )
            )
        }
        else -> error("Unknown Message Type! type $messageType")
    }
}