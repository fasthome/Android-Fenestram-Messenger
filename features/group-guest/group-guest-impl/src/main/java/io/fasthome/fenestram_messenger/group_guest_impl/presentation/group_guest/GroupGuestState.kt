/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem

data class GroupGuestState(
    val contacts: List<ContactViewItem>,
    val addedContacts: List<ContactViewItem>,
    val needScroll: Boolean
)