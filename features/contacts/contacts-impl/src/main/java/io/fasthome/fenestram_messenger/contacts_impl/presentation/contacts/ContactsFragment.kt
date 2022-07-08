/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class ContactsFragment : BaseFragment<ContactsState, ContactsEvent>(R.layout.fragment_contacts) {

    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm : ContactsViewModel by viewModel(
        getParamsInterface = ContactsNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface),
    )

    override fun renderState(state: ContactsState) = nothingToRender()

    override fun handleEvent(event: ContactsEvent) = noEventsExpected()

}