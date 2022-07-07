/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.databinding.FragmentContactsBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel


class ContactsFragment : BaseFragment<ContactsState, ContactsEvent>(R.layout.fragment_contacts) {

    override val vm: ContactsViewModel by viewModel()

    private val binding: FragmentContactsBinding by fragmentViewBinding(FragmentContactsBinding::bind)

    private lateinit var contactsAdapter: ContactsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsAdapter = ContactsAdapter()
        binding.contactsList.adapter = contactsAdapter

        binding.contactsAdd.setOnClickListener {
            vm.fetchContacts()
        }
    }

    override fun renderState(state: ContactsState) {
        when {
            state.contacts.isNotEmpty() -> contactsAdapter.items = state.contacts
            state.contacts.isEmpty() -> contactsAdapter.items =
                listOf(ContactsViewItem(0, 0, "Nothing", 0))
        }
    }

    override fun handleEvent(event: ContactsEvent) = noEventsExpected()

}