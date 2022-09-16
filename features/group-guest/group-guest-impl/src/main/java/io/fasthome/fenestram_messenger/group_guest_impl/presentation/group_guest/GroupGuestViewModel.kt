/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.group_guest_impl.domain.logic.AddUsersUseCase
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.mapper.mapToContactViewItem
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.model.AddContactViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class GroupGuestViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val params: GroupGuestContract.Params,
    private val contactsFeature: ContactsFeature,
    private val addUsersUseCase: AddUsersUseCase
) : BaseViewModel<GroupGuestState, GroupGuestEvent>(router, requestParams) {

    private var originalContacts = listOf<Contact>()

    init {
        viewModelScope.launch {
            contactsFeature.getContacts().onSuccess {
                originalContacts = it.filter { contact ->
                    contact.user != null
                }.filter { contact ->
                    params.participantsParams.find { user ->
                        user.userId == contact.user!!.id
                    } == null
                }
                updateState { state ->
                    state.copy(
                        contacts = originalContacts.map(::mapToContactViewItem) + AddContactViewItem.Footer(
                            link = "test/link"
                        )
                    )
                }
            }
        }
    }

    override fun createInitialState(): GroupGuestState {
        return GroupGuestState(listOf(), listOf())
    }

    fun onBackClick() {
        exitWithoutResult()
    }

    fun onContactClicked(addContactViewItem: AddContactViewItem.AddContact) {
        updateState { state ->
            val newList = if (addContactViewItem.isSelected) {
                state.addedContacts.filter { if (it is AddContactViewItem.AddContact) it.userId != addContactViewItem.userId else true }
            } else {
                state.addedContacts.plus(addContactViewItem)
            }
            state.copy(addedContacts = newList)
        }
    }

    fun onLinkFieldClick(link: String) {
        sendEvent(GroupGuestEvent.CopyTextEvent(link))
    }

    fun onAddClick() {
        val usersId =
            currentViewState.addedContacts.mapNotNull { if (it is AddContactViewItem.AddContact) it.userId else null }
        viewModelScope.launch {
            val usersAdded = addUsersUseCase.addUsersToChat(
                params.chatId,
                usersId
            ).successOrSendError()
            if (usersAdded != null)
                exitWithResult(
                    GroupGuestContract.createResult(
                        GroupGuestContract.Result.UsersAdded(users = usersAdded)
                    )
                )
            else
                exitWithResult(GroupGuestContract.createResult(GroupGuestContract.Result.Canceled))
        }
    }

}