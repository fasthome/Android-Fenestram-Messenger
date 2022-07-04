/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.presentation.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commitNow
import io.fasthome.fenestram_messenger.main_impl.R
import io.fasthome.fenestram_messenger.main_impl.databinding.FragmentMainBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class MainFragment : BaseFragment<MainState, MainEvent>(R.layout.fragment_main) {

    override val vm: MainViewModel by viewModel()

    private val binding : FragmentMainBinding by fragmentViewBinding(FragmentMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigationView.setOnItemSelectedListener { item ->
            val tabType = TabsMapper.mapItemIdToTab(item) ?: return@setOnItemSelectedListener false
            vm.onShowFragment(tabType)
            true
        }
    }

    override fun renderState(state: MainState) {
        val tabToOpen = state.currentTab

        binding.navigationView.selectedItemId = TabsMapper.mapTabToItemId(tabToOpen)

        childFragmentManager.commitNow {
            childFragmentManager.fragments
                .filter { it.tag != tabToOpen.name }
                .forEach { detach(it) }

            val existedFragment = childFragmentManager.findFragmentByTag(tabToOpen.name)
            when {
                existedFragment == null -> {
                    val newFragment = vm.buildFragment(tabToOpen)
                    add(binding.content.id, newFragment, tabToOpen.name)
                }
                existedFragment.isDetached -> {
                    attach(existedFragment)
                }
            }
        }
    }

    override fun handleEvent(event: MainEvent) = noEventsExpected()
}