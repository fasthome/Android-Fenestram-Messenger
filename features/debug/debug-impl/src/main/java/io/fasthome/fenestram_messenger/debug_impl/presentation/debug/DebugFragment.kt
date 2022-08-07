/**
 * Created by Dmitry Popov on 18.07.2022.
 */
package io.fasthome.fenestram_messenger.debug_impl.presentation.debug

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.debug_impl.R
import io.fasthome.fenestram_messenger.debug_impl.databinding.FragmentDebugBinding
import io.fasthome.fenestram_messenger.mvi.BaseViewEvent
import io.fasthome.fenestram_messenger.mvi.ViewModelInterface
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick

class DebugFragment : BaseFragment<DebugState, DebugEvent>(R.layout.fragment_debug) {

    private val binding by fragmentViewBinding(FragmentDebugBinding::bind)

    override val vm : DebugViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        debugAuth.onClick {
            vm.onAuthClicked()
        }

        debugProfileGuest.onClick {
            vm.onProfileGuestClicked()
        }
        debugLogout.onClick {
            vm.onLogoutClicked()
        }
    }

    override fun renderState(state: DebugState) = nothingToRender()
    override fun handleEvent(event: DebugEvent) = noEventsExpected()

}