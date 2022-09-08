package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentWelcomeBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class WelcomeFragment : BaseFragment<WelcomeState, WelcomeEvent>(R.layout.fragment_welcome) {

    override val vm: WelcomeViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentWelcomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            appName.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.common_fenestram_label))

            buttonSendCode.setOnClickListener {
                vm.checkPhoneNumber(binding.phoneInput.unMasked)
            }

            phoneInput.addTextChangedListener {
                vm.overWritePhoneNumber()
            }
        }
    }

    override fun renderState(state: WelcomeState): Unit = with(binding) {
        if (state.error)
            phoneInput.setBackgroundResource(R.drawable.error_rounded_border)
        else
            phoneInput.setBackgroundResource(R.drawable.rounded_border)
        buttonSendCode.loading(state.isLoad)
    }

    override fun handleEvent(event: WelcomeEvent): Unit = noEventsExpected()

    private fun Button.loading(isLoad: Boolean) {
        val originalText = this.text
        binding.progress.isVisible = isLoad
        if (isLoad) {
            this.text = ""
        } else {
            this.text = originalText
        }
    }
}