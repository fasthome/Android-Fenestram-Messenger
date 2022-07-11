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

    override fun renderState(state: CodeState): Unit = with(binding) {
        when {
            state.filled -> {
                buttonSendCode.apply {
                    isEnabled = true
                    setBackgroundResource(R.drawable.rounded_button)
                }
            }
            state.error -> {
                codeInput.apply { setBackgroundResource(R.drawable.error_rounded_border) }
                resendCode.apply { setTextColor(resources.getColor(R.color.auth_error, null)) }
                error.apply { visibility = View.VISIBLE }
                buttonSendCode.apply { isEnabled = false }
            }
            else -> {
                codeInput.apply { setBackgroundResource(R.drawable.rounded_border) }
                resendCode.apply { setTextColor(resources.getColor(R.color.auth_button, null)) }
                error.apply { visibility = View.GONE }
                buttonSendCode.apply {
                    isEnabled = false
                    setBackgroundResource(R.drawable.rounded_gray_button)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            appName.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.auth_fenestram_label))

            buttonSendCode.setOnClickListener {
                vm.checkCode(binding.codeInput.text.toString())
            }

            codeInput.addTextChangedListener {
                vm.overWriteCode(binding.codeInput.text.toString())
            }

            resendCode.setOnClickListener {
                // TODO ПОВТОРНАЯ ОТПРАВКА КОДА
            }
        }
    }

    override fun handleEvent(event: CodeEvent): Unit = noEventsExpected()

}