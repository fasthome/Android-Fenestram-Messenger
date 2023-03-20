package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentCodeBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.onClick
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        appName.includeWelcomeText.setText(R.string.common_hoolichat_label)

        buttonSendCode.onClick {
            vm.checkCode(binding.codeInput.text.toString())
        }

        codeInput.addTextChangedListener {
            vm.validateCode(binding.codeInput.text.toString())
        }

        requireActivity().registerReceiver(
            smsReceiver,
            IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        )

        resendCode.onClick {
            vm.resendCode()
        }
    }


    override fun renderState(state: CodeState): Unit = with(binding) {
        phone.setPrintableText(state.phone)
        state.autoFilling?.let {
            fillCode(it)
        }
        state.error?.let {
            renderError()
        }
        buttonSendCode.loading(state.loading)
    }

    override fun handleEvent(event: CodeEvent): Unit = with(binding) {
        when (event) {
            is CodeEvent.ChangeTime -> {
                if (event.time.isNullOrEmpty()) {
                    resendCode.apply {
                        setPrintableText(PrintableText.StringResource(R.string.auth_resend_code_button))
                        isClickable = true
                    }

                } else {
                    resendCode.apply {
                        text = getString(R.string.auth_resend_code_timer, event.time)
                        isClickable = false
                    }
                }
            }
            is CodeEvent.ValidateCode -> {
                buttonSendCode.isEnabled = event.isValid
                renderNormalState()
            }
        }
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(smsReceiver)
        super.onDestroy()
    }

    private fun fillCode(code: String) = with(binding.codeInput) {
        setText(code)
        clearFocus()
        setBackgroundResource(R.drawable.rounded_border)
    }

    private fun renderNormalState() = with(binding) {
        codeInput.setBackgroundResource(R.drawable.rounded_border)
        resendCode.setTextColor(resources.getColor(R.color.main_active, null))
        error.isVisible = false
    }

    private fun renderError() = with(binding) {
        codeInput.setBackgroundResource(R.drawable.error_rounded_border)
        resendCode.setTextColor(resources.getColor(R.color.red, null))
        error.isVisible = true
    }

    private fun Button.loading(isLoad: Boolean?) {
        if (isLoad == null) {
            return
        }
        binding.progress.isVisible = isLoad
        binding.codeInput.isEnabled = !isLoad
        this.isEnabled = !isLoad
        if (isLoad) {
            this.text = ""
        } else {
            this.setText(R.string.auth_ready_button)
        }
    }

}