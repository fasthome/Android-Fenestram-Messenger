package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentCodeBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class CodeFragment : BaseFragment<CodeState, CodeEvent>(R.layout.fragment_code) {
    override val vm: CodeViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentCodeBinding::bind)

    override fun renderState(state: CodeState) = with(binding) {
        when {
            state.filled -> {
                buttonSendCode.isEnabled = true
                buttonSendCode.setBackgroundResource(R.drawable.rounded_button)
            }
            state.error -> {
                codeInput.setBackgroundResource(R.drawable.error_rounded_border)
                resendCode.setTextColor(resources.getColor(R.color.error, null))
                error.visibility = View.VISIBLE
                buttonSendCode.isEnabled = false
            }
            else -> {
                codeInput.setBackgroundResource(R.drawable.rounded_border)
                resendCode.setTextColor(resources.getColor(R.color.button, null))
                error.visibility = View.GONE
                buttonSendCode.isEnabled = false
                buttonSendCode.setBackgroundResource(R.drawable.rounded_gray_button)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appName.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.fenestram_label))

        binding.buttonSendCode.setOnClickListener {
            vm.checkCode(binding.codeInput.text.toString())
        }

        binding.codeInput.addTextChangedListener {
            vm.overWriteCode(binding.codeInput.text.toString())
        }

        binding.resendCode.setOnClickListener {
            // TODO ПОВТОРНАЯ ОТПРАВКА КОДА
        }
    }

    override fun handleEvent(event: CodeEvent): Unit = noEventsExpected()

}