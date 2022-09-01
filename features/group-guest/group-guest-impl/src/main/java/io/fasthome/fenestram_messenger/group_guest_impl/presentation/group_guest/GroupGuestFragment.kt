/**
 * Created by Dmitry Popov on 31.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest

import io.fasthome.fenestram_messenger.group_guest_impl.databinding.FragmentGroupGuestBinding
import io.fasthome.fenestram_messenger.mvi.BaseViewEvent
import io.fasthome.fenestram_messenger.mvi.ViewModelInterface
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class GroupGuestFragment : BaseFragment<GroupGuestState, GroupGuestEvent>() {

    override val vm : GroupGuestViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentGroupGuestBinding::bind)

    override fun renderState(state: GroupGuestState) {

    }

    override fun handleEvent(event: GroupGuestEvent) = noEventsExpected()


}