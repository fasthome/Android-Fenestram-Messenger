/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.presentation.main

import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.main_impl.R
import io.fasthome.fenestram_messenger.main_impl.databinding.FragmentMainBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class MainFragment : BaseFragment<MainState, MainEvent>(R.layout.fragment_main) {

    override val vm: MainViewModel by viewModel()

    private val binding : FragmentMainBinding by fragmentViewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding){
        super.onViewCreated(view, savedInstanceState)

    }

    override fun renderState(state: MainState) {

    }

    override fun handleEvent(event: MainEvent) = noEventsExpected()
}