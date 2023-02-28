package io.fasthome.fenestram_messenger.settings_impl.presentation.settings


import android.graphics.Rect
import android.os.Bundle
import android.view.View
import io.fasthome.fenestram_messenger.navigation.contract.InterfaceFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.settings_api.SettingsInterface
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.settings_impl.databinding.FragmentSettingsBinding
import io.fasthome.fenestram_messenger.settings_impl.presentation.DeleteAccountDialog
import io.fasthome.fenestram_messenger.settings_impl.presentation.LogoutDialog
import io.fasthome.fenestram_messenger.settings_impl.presentation.settings.adapter.SettingsAdapter
import io.fasthome.fenestram_messenger.uikit.SpacingItemDecoration
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.dp


class SettingsFragment : BaseFragment<SettingsState, SettingsEvent>(R.layout.fragment_settings),
    InterfaceFragment<SettingsInterface> {

    override val vm: SettingsViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentSettingsBinding::bind)

    private val adapter = SettingsAdapter()

    private val decoration = SpacingItemDecoration { index, itemCount ->
        Rect(
            20.dp,
            16.dp,
            20.dp,
            0,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
        list.itemAnimator = null
        list.removeItemDecoration(decoration)
        list.addItemDecoration(decoration)
    }

    override fun renderState(state: SettingsState) = with(binding) {
        adapter.items = state.items
    }

    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.DeleteAccount -> {
                DeleteAccountDialog.create(
                    fragment = this,
                    titleText = PrintableText.StringResource(R.string.settings_delete_accept_title),
                    messageText = PrintableText.StringResource(R.string.settings_delete_accept_description),
                    onAcceptClicked = vm::deleteAccount
                ).show()
            }
            is SettingsEvent.Logout -> {
                LogoutDialog.create(
                    fragment = this,
                    titleText = PrintableText.StringResource(R.string.settings_logout_title),
                    onAcceptClicked = vm::logout
                ).show()
            }
        }
    }

    override fun getInterface(): SettingsInterface = vm

    override fun syncTheme(appTheme: Theme) = with(binding){
        appTheme.setContext(requireActivity().applicationContext)
        vm.themeSynced(appTheme)
    }
}