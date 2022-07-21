package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentCodeBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getPrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class CodeFragment : BaseFragment<CodeState, CodeEvent>(R.layout.fragment_code) {
    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm: CodeViewModel by viewModel(
        getParamsInterface = CodeNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    private val smsReceiver = SmsReceiver {
        vm.autoFillCode(this)
    }

    private val binding by fragmentViewBinding(FragmentCodeBinding::bind)

    override fun renderState(state: CodeState): Unit = with(binding) {
        when (state) {
            is CodeState.GlobalState -> {
                when {
                    !state.autoFilling.isNullOrEmpty() -> {
                        codeInput.apply {
                            setText(state.autoFilling)
                            clearFocus()
                            setBackgroundResource(R.drawable.rounded_border)
                        }
                        resendCode.setTextColor(resources.getColor(R.color.auth_button, null))
                        error.isVisible = false
                    }

                    state.filled -> {
                        buttonSendCode.apply {
                            isEnabled = true
                            setBackgroundResource(R.drawable.rounded_button)
                        }
                    }
                    state.error -> {
                        codeInput.setBackgroundResource(R.drawable.error_rounded_border)
                        resendCode.setTextColor(resources.getColor(R.color.auth_error, null))
                        error.isVisible = true
                        buttonSendCode.isEnabled = false
                    }
                    else -> {
                        codeInput.setBackgroundResource(R.drawable.rounded_border)
                        resendCode.setTextColor(resources.getColor(R.color.auth_button, null))
                        error.isVisible = false
                        buttonSendCode.apply {
                            isEnabled = false
                            setBackgroundResource(R.drawable.rounded_gray_button)
                        }
                    }
                }
            }
            is CodeState.ChangeTime -> {
                if (state.time.isNullOrEmpty())
                    resendCode.apply {
                        setPrintableText(PrintableText.StringResource(R.string.auth_resend_code_button))
                        resendCode.isClickable = true

                    }
                else
                    resendCode.apply {
                        text =
                            resources.getPrintableText(PrintableText.StringResource(R.string.auth_resend_code_timer)) + state.time
                        isClickable = false
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

            requireActivity().registerReceiver(
                smsReceiver,
                IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            )
        }

    }

    override fun handleEvent(event: CodeEvent) {
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


    override fun onDestroy() {
        requireActivity().unregisterReceiver(smsReceiver)
        super.onDestroy()
    }

}