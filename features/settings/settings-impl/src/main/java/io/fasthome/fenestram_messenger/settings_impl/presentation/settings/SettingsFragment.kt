package io.fasthome.fenestram_messenger.settings_impl.presentation.settings


import android.os.Bundle
import android.view.View
import com.instabug.library.Instabug
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.settings_impl.R
import io.fasthome.fenestram_messenger.settings_impl.databinding.FragmentSettingsBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick


class SettingsFragment : BaseFragment<SettingsState, SettingsEvent>(R.layout.fragment_settings) {

    override val vm: SettingsViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        hooliToolbar.setOnButtonClickListener {
            onBackPressed()
        }

        tvExitProfile.onClick {
            vm.onLogoutClicked()
        }

        tvAboutApp.onClick {
            vm.startInfoapp()
        }

        tvBugReport.onClick {
            Instabug.show()
        }

        ibBlueButton.onClick {
            vm.onBlueClicked()
        }

        ibGreenButton.onClick {
            vm.onGreenClicked()
        }

        tvDeleteAccount.onClick {
            vm.onDeleteAccountClicked()
        }
    }

    override fun renderState(state: SettingsState) = with(binding) {
        ibBlueButton.isActivated = state.blueSelected
        ibGreenButton.isActivated = state.greenSelected
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
}