package io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.group_guest_api.GroupParticipantsInterface
import io.fasthome.fenestram_messenger.group_guest_impl.R
import io.fasthome.fenestram_messenger.group_guest_impl.databinding.FragmentGroupParticipantsBinding
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.participants.adapter.ParticipantsAdapter
import io.fasthome.fenestram_messenger.navigation.contract.InterfaceFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.supportBottomSheetScroll

class GroupParticipantsFragment :
    BaseFragment<GroupParticipantsState, GroupParticipantsEvent>(R.layout.fragment_group_participants),
    InterfaceFragment<GroupParticipantsInterface> {

    override val vm: GroupParticipantsViewModel by viewModel(
        getParamsInterface = GroupParticipantsComponentContract.getParams
    )

    private val adapter = ParticipantsAdapter()

    private val binding by fragmentViewBinding(FragmentGroupParticipantsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.participants.adapter = adapter
        binding.participants.supportBottomSheetScroll()
    }

    override fun renderState(state: GroupParticipantsState) {
        adapter.items = state.participants
    }

    override fun handleEvent(event: GroupParticipantsEvent) = noEventsExpected()

    override fun getInterface(): GroupParticipantsInterface = vm

}