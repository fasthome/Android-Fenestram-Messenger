package io.fasthome.component.bottom_navigation

import io.fasthome.component.R
import io.fasthome.component.databinding.FragmentBottomNavigationBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class BottomNavFragment :
    BaseFragment<BottomNavState, BottomNavEvent>(R.layout.fragment_bottom_navigation) {

    private val binding by fragmentViewBinding(FragmentBottomNavigationBinding::bind)

    override val vm: BottomNavViewModel by viewModel()

    override fun handleEvent(event: BottomNavEvent) = noEventsExpected()

    override fun renderState(state: BottomNavState) {

    }

}