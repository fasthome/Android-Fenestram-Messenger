/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger

import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.presentation.messenger.model.MessengerViewItem

data class MessengerState(
    val chats: List<Chat>,
    val messengerViewItems: List<MessengerViewItem>,
    val newMessagesCount: Int
)