/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.model.AddContactViewItem

data class GroupGuestState(
    val contacts: List<AddContactViewItem>,
    val addedContacts: List<AddContactViewItem>
)