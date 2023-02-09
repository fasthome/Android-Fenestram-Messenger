package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model

import androidx.annotation.DrawableRes
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.SentStatus
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.model.getStatusIcon
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.model.MetaInfo

data class MessengerViewItem(
    val id: Long,
    @DrawableRes val avatar: Int,
    val name: PrintableText,
    val newMessages: Int,
    val time: PrintableText,
    val lastMessage: LastMessage,
    val profileImageUrl: String?,
    val originalChat: Chat,
    val isGroup: Boolean,
    val sentStatus: SentStatus,
    @DrawableRes val statusIcon: Int = getStatusIcon(sentStatus),
    val pendingAmount: PrintableText
)

sealed class LastMessage {

    data class Text(val text: PrintableText) : LastMessage()
    data class UserStatus(val status: PrintableText) : LastMessage()
    data class Image(val imageUrl: List<MetaInfo>) : LastMessage()
    object Document : LastMessage()

}