/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants

import androidx.lifecycle.viewModelScope
import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.domain.entity.Chat
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.create_info.CreateInfoContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.mapper.mapToContactViewItem
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.model.ContactViewItem
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onSuccess
import kotlinx.coroutines.launch

class CreateGroupChatViewModel(
    requestParams: RequestParams,
    router: ContractRouter,
    private val contactsFeature: ContactsFeature,
    private val params: CreateGroupChatContract.Params
) : BaseViewModel<CreateGroupChatState, CreateGroupChatEvent>(router, requestParams) {

    private val createInfoLauncher = registerScreen(CreateInfoContract)
    private var originalContacts = listOf<Contact>()

    private val conversationLauncher = registerScreen(ConversationNavigationContract) { }

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

    override fun createInitialState(): CreateGroupChatState {
        return CreateGroupChatState(listOf(), listOf(), false, params.isGroupChat)
    }

    fun onContactClicked(contactViewItem: ContactViewItem) {
        updateState { state ->
            val newList = if (contactViewItem.isSelected) {
                state.addedContacts.filter { it.userId != contactViewItem.userId }
            } else {
                if (params.isGroupChat)
                    state.addedContacts.plus(contactViewItem)
                else
                    state.addedContacts.dropLast(1).plus(contactViewItem)
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

    fun onNextClicked() {
        if (currentViewState.addedContacts.isEmpty()) {
            showPopup(PrintableText.StringResource(R.string.messenger_select_hint))
            return
        }
        val contacts = currentViewState.addedContacts.mapNotNull { viewItem ->
            originalContacts.find { contact ->
                viewItem.userId == contact.userId
            }
        }

        if (params.isGroupChat)
            createInfoLauncher.launch(CreateInfoContract.Params(contacts))
        else {
            router.backTo(null)
            conversationLauncher.launch(
                ConversationNavigationContract.Params(
                    chat = Chat(
                        name = contacts[0].userName ?: "",
                        users = listOf(contacts[0].user!!.id),
                        isGroup = false,
                        time = null,
                        messages = listOf(),
                        avatar = contacts[0].user!!.avatar,
                        id = null,
                    )
                )
            )
        }
    }

    fun filterContacts(text: String) {
        val filteredContacts = if (text.isEmpty()) {
            originalContacts
        } else {
            originalContacts.filter {
                it.userName?.contains(text.trim(), true) ?: false
            }
        }
        updateState { state ->
            state.copy(
                contacts = filteredContacts.map(::mapToContactViewItem)
            )
        }
    }

    override fun onBackPressed(): Boolean {
        exitWithoutResult()
        return true
    }

}