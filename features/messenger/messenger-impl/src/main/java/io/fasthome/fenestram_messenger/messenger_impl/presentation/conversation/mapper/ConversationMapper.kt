package io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.messenger_api.entity.MessageInfo
import io.fasthome.fenestram_messenger.messenger_api.entity.MessageType
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Message
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.MessageStatus
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.UserStatus
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.*
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
const val MESSAGE_TYPE_DOCUMENT = "documents"
const val MESSAGE_TYPE_VOICE = "voices"
const val MESSAGE_TYPE_IMAGES = "images"
const val MESSAGE_TYPE_VIDEOS = "videos"
const val MESSAGE_TYPE_AUDIOS = "audios"

const val MESSAGE_STATUS_SENT = "sent"
const val MESSAGE_STATUS_RECEIVED = "pending"
const val MESSAGE_STATUS_READ = "read"

fun List<Message>.toConversationItems(
    selfUserId: Long?,
    isGroup: Boolean,
    profileImageUrlConverter: StorageUrlConverter,
): Map<String, ConversationViewItem> {
    val resultMap: MutableMap<String, ConversationViewItem> = mutableMapOf()
    this.forEach {
        if (it.forwardedMessages.isNullOrEmpty()) {
            resultMap[UUID.randomUUID().toString()] =
                it.toConversationViewItem(selfUserId, isGroup, profileImageUrlConverter)
        } else {
            resultMap += it.toForwardConversationViewItem(
                selfUserId,
                isGroup,
                profileImageUrlConverter
            )
        }
    }
    return resultMap
}

fun Message.toForwardConversationViewItem(
    selfUserId: Long? = null,
    isGroup: Boolean,
    profileImageUrlConverter: StorageUrlConverter,
): Map<String, ConversationViewItem> {
    if (forwardedMessages == null) return emptyMap()
    val resultMap: MutableMap<String, ConversationViewItem> = mutableMapOf()
    this.forwardedMessages.forEach { message ->
        val sentStatus = getSentStatus(messageStatus)
        val localId = UUID.randomUUID().toString()
        val conversationViewItem: ConversationViewItem = when {
            (selfUserId == this.userSenderId) -> ConversationViewItem.Self.Forward(
                content = PrintableText.EMPTY,
                time = PrintableText.Raw(timeFormatter.format(date)),
                sentStatus = sentStatus,
                date = date,
                id = id,
                localId = localId,
                timeVisible = true,
                nickname = initiator?.nickname,
                messageType = messageType,
                replyMessage = null,
                userName = PrintableText.Raw(getName(initiator)),
                forwardMessage = message.toConversationViewItem(
                    selfUserId,
                    isGroup,
                    profileImageUrlConverter
                )
            )
            else -> {
                if (isGroup) {
                    ConversationViewItem.Group.Forward(
                        content = PrintableText.EMPTY,
                        time = PrintableText.Raw(timeFormatter.format(date)),
                        sentStatus = sentStatus,
                        date = date,
                        id = id,
                        timeVisible = true,
                        nickname = initiator?.nickname,
                        messageType = messageType,
                        replyMessage = null,
                        userName = PrintableText.Raw(getName(initiator)),
                        forwardMessage = message.toConversationViewItem(
                            selfUserId,
                            isGroup,
                            profileImageUrlConverter
                        ),
                        avatar = initiator?.avatar ?: "",
                        phone = initiator?.phone ?: "",
                        userId = initiator?.id ?: 0
                    )
                } else {
                    ConversationViewItem.Receive.Forward(
                        content = PrintableText.EMPTY,
                        time = PrintableText.Raw(timeFormatter.format(date)),
                        sentStatus = sentStatus,
                        date = date,
                        id = id,
                        timeVisible = true,
                        nickname = initiator?.nickname,
                        messageType = messageType,
                        replyMessage = null,
                        userName = PrintableText.Raw(getName(initiator)),
                        forwardMessage = message.toConversationViewItem(
                            selfUserId,
                            isGroup,
                            profileImageUrlConverter
                        )
                    )
                }
            }
        }
        resultMap[localId] = conversationViewItem
    }
    return resultMap
}

/***
 * @param selfUserId ID текущего пользователя
 * @param isGroup Группа ли
 * @param profileImageUrlConverter Для преобразования path в link, todo Выделить маппер в объект
 * @param isForwardMessage пересланное или ответ на сообщение - всегда новое сообщение, поэтому первый статус сообщения должен быть [SentStatus.Sent]
 */
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

    val sentStatus = if (!this.forwardedMessages.isNullOrEmpty() || this.replyMessage != null) {
        SentStatus.Sent
    } else {
        if (userSenderId == selfUserId && usersHaveRead?.isNotEmpty() == true) SentStatus.Read else SentStatus.Received
    }

    return when {
        (selfUserId == userSenderId) -> {
            when (messageType) {
                MESSAGE_TYPE_TEXT -> {
                    when {
                        (replyMessage?.messageType == MESSAGE_TYPE_TEXT || replyMessage == null) -> {
                            ConversationViewItem.Self.Text(
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
                        }
                        else -> {
                            ConversationViewItem.Self.TextReplyOnImage(
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
                                replyMessage = replyMessage!!.toConversationViewItem(
                                    selfUserId,
                                    isGroup,
                                    profileImageUrlConverter
                                ),
                                userName = PrintableText.Raw(getName(initiator))
                            )
                        }
                    }
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
                        files = null,
                        path = null,
                        userName = PrintableText.Raw(getName(initiator)),
                        metaInfo = content?.map { MetaInfo(it) } ?: emptyList()
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
                else -> {
                    ConversationViewItem.System(
                        content = PrintableText.Raw("Вместо этого текста, должно быть сообщение с messageType $messageType! Скоро"),
                        time = PrintableText.Raw(timeFormatter.format(date)),
                        sentStatus = SentStatus.Sent,
                        date = date,
                        id = id,
                        timeVisible = true
                    )
                }
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
                            metaInfo = content?.map { MetaInfo(it) } ?: emptyList()
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
                    else -> {
                        ConversationViewItem.System(
                            content = PrintableText.Raw("Вместо этого текста, должно быть сообщение с messageType $messageType! Скоро"),
                            time = PrintableText.Raw(timeFormatter.format(date)),
                            sentStatus = SentStatus.Sent,
                            date = date,
                            id = id,
                            timeVisible = true
                        )
                    }
                }
            } else {
                when (messageType) {
                    MESSAGE_TYPE_TEXT -> {
                        when {
                            (replyMessage?.messageType == MESSAGE_TYPE_TEXT || replyMessage == null) -> {
                                ConversationViewItem.Receive.Text(
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
                            }
                            else -> {
                                ConversationViewItem.Receive.TextReplyOnImage(
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
                        }
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
                            userName = PrintableText.Raw(getName(initiator)),
                            metaInfo = content?.map { MetaInfo(it) } ?: emptyList()
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
                    else -> ConversationViewItem.System(
                        content = PrintableText.Raw("Вместо этого текста, должно быть сообщение с messageType $messageType! Скоро"),
                        time = PrintableText.Raw(timeFormatter.format(date)),
                        sentStatus = SentStatus.Sent,
                        date = date,
                        id = id,
                        timeVisible = true
                    )
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

fun createDocumentMessage(document: List<String>, file: List<File>) = ConversationViewItem.Self.Document(
    content = "",
    time = PrintableText.Raw(timeFormatter.format(ZonedDateTime.now())),
    sentStatus = SentStatus.Loading,
    date = ZonedDateTime.now(),
    id = 0,
    localId = UUID.randomUUID().toString(),
    files = file,
    timeVisible = true,
    path = null,
    userName = PrintableText.EMPTY,
    metaInfo = file.map { MetaInfo(it) }
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

fun UserStatus.toPrintableText(
    userName: String,
    isGroup: Boolean,
    groupUsers: List<User>?
): PrintableText {
    return when (this) {
        UserStatus.OnlineStatus -> {
            if (groupUsers.isNullOrEmpty())
                PrintableText.Raw("")
            else
                if (isGroup)
                    PrintableText.StringResource(
                        resId = R.string.user_group_status,
                        groupUsers.size,
                        groupUsers.map { it.isOnline }.size
                    )
                else
                    if (groupUsers[0].isOnline)
                        PrintableText.StringResource(R.string.user_status_online)
                    else
                        PrintableText.StringResource(R.string.user_status_offline)
        }
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

fun MessageStatus.toConversationViewItem(
    oldViewItem: ConversationViewItem
): ConversationViewItem {
    val sentStatus =
        if (usersHaveRead?.isNotEmpty() == true) SentStatus.Read else SentStatus.Received
    return when (messageType) {
        MESSAGE_TYPE_TEXT -> {
            return when {
                (oldViewItem as? ConversationViewItem.Self.Text) != null -> oldViewItem.copy(
                    sentStatus = sentStatus
                )
                (oldViewItem as? ConversationViewItem.Self.TextReplyOnImage) != null -> oldViewItem.copy(
                    sentStatus = sentStatus
                )
                (oldViewItem as? ConversationViewItem.Self.Forward) != null -> oldViewItem.copy(
                    sentStatus = getSentStatus(
                        messageStatus
                    )
                )
                else -> error("Unknown View Item $oldViewItem !")
            }
        }
        MESSAGE_TYPE_IMAGE -> {
            (oldViewItem as ConversationViewItem.Self.Image).copy(
                sentStatus = sentStatus
            )
        }

        MESSAGE_TYPE_DOCUMENT -> {
            (oldViewItem as ConversationViewItem.Self.Document).copy(
                sentStatus = sentStatus
            )
        }
        MESSAGE_TYPE_SYSTEM -> {
            (oldViewItem as ConversationViewItem.System).copy(
                sentStatus = sentStatus
            )
        }
        else -> error("Unknown Message Type! type $messageType")
    }
}

fun ConversationViewItem.toMessageInfo(): MessageInfo {
    val type = when (this) {
        is ConversationTextItem -> {
            MessageType.Text
        }
        is ConversationImageItem -> {
            MessageType.Image
        }
        is ConversationDocumentItem -> {
            MessageType.Document
        }
        else -> {
            MessageType.Unknown
        }
    }
    return MessageInfo(
        id = id,
        type = type
    )
}

fun findForwardText(messageToForward: MessengerFeature.ForwardMessage) =
    when (messageToForward.message.type) {
        MessageType.Text -> PrintableText.StringResource(
            R.string.forward_messages_ph,
            getPrintableRawText(messageToForward.username)
        )
        MessageType.Image -> PrintableText.StringResource(
            R.string.forward_image_from_ph,
            getPrintableRawText(messageToForward.username)
        )
        MessageType.Document -> PrintableText.StringResource(
            R.string.forward_file_from_ph,
            getPrintableRawText(messageToForward.username)
        )
        MessageType.Unknown -> PrintableText.StringResource(
            R.string.forward_messages_ph,
            getPrintableRawText(messageToForward.username)
        )
    }
