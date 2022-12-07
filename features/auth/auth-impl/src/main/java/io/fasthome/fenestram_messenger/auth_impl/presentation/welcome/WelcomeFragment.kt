package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentWelcomeBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
import io.fasthome.fenestram_messenger.util.setPrintableText


class WelcomeFragment : BaseFragment<WelcomeState, WelcomeEvent>(R.layout.fragment_welcome) {

    override val vm: WelcomeViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentWelcomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            debug.onClick{
                vm.debugClicked()
            }

            appName.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.common_hoolichat_label))

            buttonSendCode.setOnClickListener {
                vm.checkPhoneNumber(
                    binding.phoneInput.getPhoneNumberFiltered(),
                    binding.phoneInput.isValid()
                )
            }

            phoneInput.addListener {
                vm.overWritePhoneNumber()
            }
        }
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return super.onBackPressed()
    }

    override fun renderState(state: WelcomeState): Unit = with(binding) {
        if (state.error)
            phoneInput.setBackground(R.drawable.error_rounded_border)
        else
            phoneInput.setBackground(R.drawable.rounded_border)
        buttonSendCode.loading(state.isLoad)
    }

    override fun handleEvent(event: WelcomeEvent): Unit = noEventsExpected()

    private fun Button.loading(isLoad: Boolean) {
        binding.progress.isVisible = isLoad
        if (isLoad) {
            this.text = ""
        } else {
            this.setPrintableText(PrintableText.StringResource(R.string.common_send_code_button))
        }
    }
}