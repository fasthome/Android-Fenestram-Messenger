package io.fasthome.fenestram_messenger.settings_impl.presentation.settings


import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.nothingToRender
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.settings_impl.R


class SettingsFragment: BaseFragment<SettingsState, SettingsEvent>(R.layout.fragment_settings) {

    override val vm: SettingsViewModel by viewModel()

    override fun renderState(state: SettingsState) = nothingToRender()

    override fun handleEvent(event: SettingsEvent) = noEventsExpected()


}