/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants

import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem

data class CreateGroupChatState(
    val contacts: List<ContactViewItem>,
    val addedContacts: List<ContactViewItem>,
    val needScroll: Boolean,
    val isGroupChat: Boolean,
    val permissionGranted: Boolean
)