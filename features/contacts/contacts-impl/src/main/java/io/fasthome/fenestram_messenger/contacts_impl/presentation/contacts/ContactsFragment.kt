/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.databinding.FragmentContactsBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import org.koin.android.ext.android.inject


class ContactsFragment : BaseFragment<ContactsState, ContactsEvent>(R.layout.fragment_contacts) {

    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm: ContactsViewModel by viewModel(
        getParamsInterface = ContactsNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    private val binding: FragmentContactsBinding by fragmentViewBinding(FragmentContactsBinding::bind)
    private val contactsAdapter = ContactsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contactsList.adapter = contactsAdapter

        binding.contactsAdd.setOnClickListener {
            vm.addContact()
        }
    }

    override fun onResume() {
        super.onResume()

        binding.contactsSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                vm.filterContacts(newText)
                return false
            }
        })
    }

    override fun renderState(state: ContactsState) {
        when {
            state.contacts.isNotEmpty() -> {
                contactsAdapter.items = state.contacts
                binding.contactsSv.visibility = View.VISIBLE
                binding.contactsList.visibility = View.VISIBLE
                binding.contactsAdd.visibility = View.VISIBLE
                binding.contactsEmpty.visibility = View.GONE
                binding.contactsAddFirst.visibility = View.GONE
                binding.contactsEmptyText.visibility = View.GONE
            }
            state.contacts.isEmpty() && !state.loading -> {
                binding.contactsSv.visibility = View.GONE
                binding.contactsList.visibility = View.GONE
                binding.contactsAdd.visibility = View.GONE
                binding.contactsEmpty.visibility = View.VISIBLE
                binding.contactsAddFirst.visibility = View.VISIBLE
                binding.contactsEmptyText.visibility = View.VISIBLE
            }
            state.contacts.isEmpty() && state.loading -> {
                contactsAdapter.items = state.contacts
                binding.contactsSv.visibility = View.VISIBLE
                binding.contactsList.visibility = View.VISIBLE
                binding.contactsAdd.visibility = View.VISIBLE
                binding.contactsEmpty.visibility = View.GONE
                binding.contactsAddFirst.visibility = View.GONE
                binding.contactsEmptyText.visibility = View.GONE
            }
        }
    }

    override fun handleEvent(event: ContactsEvent) = noEventsExpected()

}