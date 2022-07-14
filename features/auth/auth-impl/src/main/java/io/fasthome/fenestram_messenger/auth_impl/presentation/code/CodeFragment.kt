package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentCodeBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class CodeFragment : BaseFragment<CodeState, CodeEvent>(R.layout.fragment_code) {
    override val vm: CodeViewModel by viewModel(getParamsInterface = CodeNavigationContract.getParams)

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
                codeInput.setBackgroundResource(R.drawable.error_rounded_border)
                resendCode.setTextColor(resources.getColor(R.color.auth_error, null))
                error.visibility = View.VISIBLE
                buttonSendCode.isEnabled = false
            }
            else -> {
                codeInput.setBackgroundResource(R.drawable.rounded_border)
                resendCode.setTextColor(resources.getColor(R.color.auth_button, null))
                error.visibility = View.GONE
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
                vm.resendCode()
            }
        }
    }

    override fun handleEvent(event: CodeEvent): Unit {
        val text = when (event) {
            is CodeEvent.ConnectionError -> {
                connectionError
            }
            is CodeEvent.IndefiniteError -> {
                indefiniteError
            }
        }
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val connectionError = "Проверьте соединение с интернетом!"
        const val indefiniteError = "Что-то пошло не так"
    }

}