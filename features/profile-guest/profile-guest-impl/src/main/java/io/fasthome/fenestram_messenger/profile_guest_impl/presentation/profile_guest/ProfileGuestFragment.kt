package io.fasthome.fenestram_messenger.profile_guest_impl.presentation.profile_guest

import android.os.Bundle
import android.system.Os.bind
import android.view.View
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_guest_impl.R
import io.fasthome.fenestram_messenger.profile_guest_impl.databinding.FragmentProfileGuestBinding
import io.fasthome.fenestram_messenger.util.onClick

class ProfileGuestFragment : BaseFragment<ProfileGuestState, ProfileGuestEvent>(R.layout.fragment_profile_guest) {

    private val binding by fragmentViewBinding(FragmentProfileGuestBinding::bind)

    override val vm : ProfileGuestViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun renderState(state: ProfileGuestState) = nothingToRender()
    override fun handleEvent(event: ProfileGuestEvent) = noEventsExpected()

}