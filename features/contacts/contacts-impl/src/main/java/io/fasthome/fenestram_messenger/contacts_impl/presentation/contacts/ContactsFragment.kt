/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.presentation.recycler_view.ContactsAdapter
import io.fasthome.fenestram_messenger.contacts_impl.presentation.recycler_view.ContactsModel
import io.fasthome.fenestram_messenger.contacts_impl.presentation.recycler_view.contactsList
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel


class ContactsFragment : BaseFragment<ContactsState, ContactsEvent>(R.layout.fragment_contacts) {

    override val vm: ContactsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val contactsAdapter = ContactsAdapter()
        val rv: RecyclerView = view.findViewById(R.id.contacts_list)

        rv.adapter = contactsAdapter
        contactsAdapter.items = contactsList

        val fab: View = view.findViewById(R.id.contacts_add)
        fab.setOnClickListener {
            contactsList.add(
                ContactsModel(
                    id = 4,
                    name = "Noname",
                    avatar = R.drawable.bg_account_circle,
                    newMessage = true
                )
            )
            contactsAdapter.items = contactsList
            contactsAdapter.notifyDataSetChanged()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun renderState(state: ContactsState) = nothingToRender()

    override fun handleEvent(event: ContactsEvent) = noEventsExpected()

}