/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.messenger_impl.R
import io.fasthome.fenestram_messenger.messenger_impl.databinding.FragmentCreateGroupChatBinding
import io.fasthome.fenestram_messenger.messenger_impl.presentation.conversation.ConversationNavigationContract
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.adapter.AddedContactsAdapter
import io.fasthome.fenestram_messenger.messenger_impl.presentation.create_group_chat.select_participants.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick

class CreateGroupChatFragment :
    BaseFragment<CreateGroupChatState, CreateGroupChatEvent>(R.layout.fragment_create_group_chat) {

    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm: CreateGroupChatViewModel by viewModel(
        getParamsInterface = ConversationNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    private val binding by fragmentViewBinding(FragmentCreateGroupChatBinding::bind)

    private val contactsAdapter = ContactsAdapter(onItemClicked = {
        vm.onContactClicked(it)
    })

    private val addedContactsAdapter = AddedContactsAdapter(onItemClicked = {

    }, onRemoveClicked = {
        vm.onContactRemoveClick(it)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        listContacts.adapter = contactsAdapter
        listAddedInChat.adapter = addedContactsAdapter
        toolbar.setOnButtonClickListener {
            onBackPressed()
        }
        next.onClick {
            vm.onNextClicked()
        }

        contactsSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    vm.filterContacts(it)
                }
                return true
            }
        })

        contactsAllow.setOnClickListener {
            vm.checkPermissionsAndLoadContacts()
        }

        vm.checkPermissionsAndLoadContacts()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun renderState(state: CreateGroupChatState) {
        binding.listContacts.isVisible = state.permissionGranted
        binding.noPermissionContainer.isVisible = !state.permissionGranted

        contactsAdapter.items = state.contacts.map { item ->
            item.isSelected = state.addedContacts.find { it.userId == item.userId } != null
            item
        }
        if (state.addedContacts.isEmpty()) binding.next.hide() else binding.next.show()

        contactsAdapter.notifyDataSetChanged()

        binding.listAddedInChat.isVisible = (state.addedContacts.isNotEmpty() && state.isGroupChat)

        if (state.isGroupChat) {
            addedContactsAdapter.items = state.addedContacts

            /***
             * Необходимо дать время binding.listAddedInChat обновить список, решается выставлением задержки для скролла
             *
             * state.addedContacts.isNotEmpty() не скролить, если список пустой
             * state.needScroll не скролить, если последний элемент был удален
             */
            if (state.addedContacts.isNotEmpty() && state.needScroll) {
                binding.listAddedInChat.postDelayed({
                    binding.listAddedInChat.smoothScrollToPosition(state.addedContacts.size - 1)
                }, 100)
            }
        }
    }

    override fun handleEvent(event: CreateGroupChatEvent) = noEventsExpected()

}