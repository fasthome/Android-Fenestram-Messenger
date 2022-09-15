/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.group_guest_impl.R
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.FragmentGroupGuestBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.adapter.ContactsAdapter
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class GroupGuestFragment : BaseFragment<GroupGuestState, GroupGuestEvent>(R.layout.fragment_group_guest) {

    override val vm : GroupGuestViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentGroupGuestBinding::bind)

    private val contactsAdapter = ContactsAdapter(onItemClicked = {
        vm.onContactClicked(it)
    })

    override fun renderState(state: GroupGuestState) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setOnButtonClickListener {
            vm.onBack()
        }
    }

    override fun handleEvent(event: GroupGuestEvent) = noEventsExpected()


}