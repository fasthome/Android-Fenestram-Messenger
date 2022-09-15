/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import android.util.Log
import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.mapper.mapToContactViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class GroupGuestViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val contactsFeature: ContactsFeature
) : BaseViewModel<GroupGuestState, GroupGuestEvent>(router, requestParams) {

    private var originalContacts = listOf<Contact>()

    init {
        viewModelScope.launch {
            contactsFeature.getContacts().onSuccess {
                originalContacts = it.filter { contact ->
                    contact.user != null
                }
                updateState { state ->
                    state.copy(contacts = originalContacts.map(::mapToContactViewItem))
                }
            }
        }
    }

    override fun createInitialState(): GroupGuestState {
        return GroupGuestState(listOf(), listOf(), false)
    }

    fun onBack() {
        exitWithoutResult()
    }

    fun onContactClicked(contactViewItem: ContactViewItem) {
        updateState { state ->
            val newList = if (contactViewItem.isSelected) {
                state.addedContacts.filter { it.userId != contactViewItem.userId }
            } else {
                state.addedContacts.plus(contactViewItem)
            }
            state.copy(addedContacts = newList, needScroll = true)
        }
    }

}