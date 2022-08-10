package io.fasthome.fenestram_messenger.settings_impl.presentation.settings


import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.*
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.settings_impl.databinding.FragmentSettingsBinding
import io.fasthome.fenestram_messenger.util.dp
import io.fasthome.fenestram_messenger.util.increaseHitArea
import io.fasthome.fenestram_messenger.util.onClick


class SettingsFragment: BaseFragment<SettingsState, SettingsEvent>(R.layout.fragment_settings) {


    override val vm: SettingsViewModel by viewModel()

    private val binding by fragmentViewBinding (FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding){
        super.onViewCreated(view, savedInstanceState)

        ibAppBar.increaseHitArea(8.dp)
        ibAppBar.onClick { vm.backProfile() }

        tvExitProfile.onClick {
            vm.onLogoutClicked()
        }

        tvAboutTheApp.setOnClickListener{
            vm.startInfoapp()
        }

        ibChangesBlue.setOnClickListener{
            vm.changesBlue()
        }
        ibChangesGreen.setOnClickListener{
            vm.changesGreen()
        }
    }

    override fun renderState(state: SettingsState) {
        binding.ibChangesBlue.isActivated = state.blueSelected
        binding.ibChangesGreen.isActivated = state.greenSelected
    }

    override fun handleEvent(event: SettingsEvent) = noEventsExpected()


}