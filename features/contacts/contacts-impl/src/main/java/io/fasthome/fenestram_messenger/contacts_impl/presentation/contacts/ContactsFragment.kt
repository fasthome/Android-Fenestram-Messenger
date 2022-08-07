/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
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
import io.fasthome.fenestram_messenger.util.isLoading
import io.fasthome.fenestram_messenger.util.renderLoadingState


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

        with(binding) {
            contactsList.adapter = contactsAdapter

            contactsAdd.setOnClickListener {
                vm.addContact()
            }

            contactsAddFirst.setOnClickListener {
                vm.addContact()
            }

            contactsAllow.setOnClickListener {
                vm.requestPermissionAndLoadContacts()
            }
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

    override fun onStop() {
        super.onStop()
        vm.stopJob()
    }

    override fun renderState(state: ContactsState) = with(binding){
        when (state.permissionGranted) {
            true -> {
                noPermissionContainer.isVisible = false
                renderLoadingState(
                    loadingState = state.loadingState,
                    progressContainer = progressContainer, errorContainer = errorContainer, contentContainer = contentContainer,
                    renderData = {
                        contactsAdapter.items = it
                    }
                )
            }
            false -> {
                errorContainer.isVisible = false
                renderLoadingState(
                    loadingState = state.loadingState,
                    progressContainer = progressContainer,
                    errorContainer = noPermissionContainer,
                    contentContainer = contentContainer,
                    renderData = {
                        contactsAdapter.items = it
                    }
                )
            }
        }
    }

    override fun handleEvent(event: ContactsEvent) {
        when(event) {
            ContactsEvent.ContactAddCancelled -> Toast.makeText(context, "Не удалось сохранить контакт", Toast.LENGTH_SHORT)
        }
    }


}