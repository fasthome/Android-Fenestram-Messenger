/**
 * Created by Dmitry Popov on 22.05.2022.
 */
package io.fasthome.fenestram_messenger.main_impl.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.commitNow
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import io.fasthome.fenestram_messenger.main_impl.R
import io.fasthome.fenestram_messenger.main_impl.databinding.BadgeViewBinding
import io.fasthome.fenestram_messenger.main_impl.databinding.FragmentMainBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.onClick


class MainFragment : BaseFragment<MainState, MainEvent>(R.layout.fragment_main) {

    override val vm: MainViewModel by viewModel()

    private val binding: FragmentMainBinding by fragmentViewBinding(FragmentMainBinding::bind)

    private val badgeView: TextView by lazy {
        val menu = binding.navigationView.getChildAt(0) as BottomNavigationMenuView
        val itemView = menu.getChildAt(1) as BottomNavigationItemView

        BadgeViewBinding.inflate(layoutInflater, itemView).notificationsBadge
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        debug.onClick {
            vm.debugClicked()
        }
        fab.onClick {
            onFabClicked()
        }
        navigationView.setItemClickListener { item ->
            val tabType = TabsMapper.mapItemIdToTab(item) ?: return@setItemClickListener false
            vm.onShowFragment(tabType)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        vm.fetchUnreadCount()
    }

    override fun renderState(state: MainState) {
        binding.debug.isVisible = state.debugVisible
        binding.fabContainer.isVisible = state.fabVisible
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

    override fun handleEvent(event: MainEvent) {
        when (event) {
            is MainEvent.UpdateBadge -> {
                badgeView.isVisible = event.count != 0
                badgeView.text = "${event.count}"
            }
        }
    }

    override fun syncTheme(appTheme: Theme) {
        appTheme.context = requireActivity().applicationContext
        binding.content.background = appTheme.bg0Color().toDrawable()
        binding.navigationView.setBackgroundResource(appTheme.navigationViewBackground())
    }

    override fun updateFabIcon(iconRes: Int?, badgeCount: Int) {
        binding.fab.setImageResource(iconRes ?: R.drawable.bg_add_contact_cross)
        binding.badge.isVisible = badgeCount != 0
        binding.badge.text = badgeCount.toString()
    }
}