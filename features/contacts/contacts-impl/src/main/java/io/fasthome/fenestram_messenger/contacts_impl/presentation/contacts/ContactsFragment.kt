/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.databinding.FragmentContactsBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel


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
                View.VISIBLE.also {
                    with(binding) {
                        contactsSv.visibility = it
                        contactsList.visibility = it
                        contactsAdd.visibility = it
                    }
                }
                View.GONE.also {
                    with(binding) {
                        contactsEmpty.visibility = it
                        contactsAddFirst.visibility = it
                        contactsEmptyText.visibility = it
                    }
                }
            }
            state.contacts.isEmpty() && !state.loading -> {
                View.GONE.also {
                    with(binding) {
                        contactsSv.visibility = it
                        contactsList.visibility = it
                        contactsAdd.visibility = it
                    }
                }
                View.VISIBLE.also {
                    with(binding) {
                        contactsEmpty.visibility = it
                        contactsAddFirst.visibility = it
                        contactsEmptyText.visibility = it
                    }
                }
            }
            state.contacts.isEmpty() && state.loading -> {
                contactsAdapter.items = state.contacts
                View.VISIBLE.also {
                    with(binding) {
                        contactsSv.visibility = it
                        contactsList.visibility = it
                        contactsAdd.visibility = it
                    }
                }
                View.GONE.also {
                    with(binding) {
                        contactsEmpty.visibility = it
                        contactsAddFirst.visibility = it
                        contactsEmptyText.visibility = it
                    }
                }
            }
        }
    }

    override fun handleEvent(event: ContactsEvent) = noEventsExpected()

}