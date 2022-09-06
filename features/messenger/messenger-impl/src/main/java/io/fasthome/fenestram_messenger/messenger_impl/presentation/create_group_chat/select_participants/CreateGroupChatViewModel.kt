/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info.CreateInfoContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.mapper.mapToContactViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class CreateGroupChatViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val contactsFeature: ContactsFeature
) : BaseViewModel<CreateGroupChatState, CreateGroupChatEvent>(router, requestParams) {

    private val createInfoLauncher = registerScreen(CreateInfoContract)
    private var originalContacts = listOf<Contact>()

    init {
        viewModelScope.launch {
            contactsFeature.getContacts().onSuccess {
                originalContacts = it.filter {  contact ->
                    contact.user != null
                }
                updateState { state ->
                    state.copy(contacts = originalContacts.map(::mapToContactViewItem))
                }
            }
        }
    }

    override fun createInitialState(): CreateGroupChatState {
        return CreateGroupChatState(listOf(), listOf(), false)
    }

    fun onContactClicked(contactViewItem: ContactViewItem) {
        updateState { state ->
            val newList = if(contactViewItem.isSelected){
                state.addedContacts.filter { it.userId != contactViewItem.userId }
            } else{
                state.addedContacts.plus(contactViewItem)
            }
            state.copy(addedContacts = newList, needScroll = true)
        }
    }

    fun onContactRemoveClick(contactViewItem: ContactViewItem) {
        updateState { state ->
            val newList = state.addedContacts.filter {
                it.userId != contactViewItem.userId
            }
            state.copy(addedContacts = newList, needScroll = false)
        }
    }

    fun onNextClicked(){
        val contacts = currentViewState.addedContacts.mapNotNull { viewItem->
            originalContacts.find { contact->
                viewItem.userId == contact.userId
            }
        }
        createInfoLauncher.launch(CreateInfoContract.Params(contacts))
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

}