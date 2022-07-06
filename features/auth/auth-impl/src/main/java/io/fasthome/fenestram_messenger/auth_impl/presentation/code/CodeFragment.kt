package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentCodeBinding
import io.fasthome.fenestram_messenger.auth_impl.presentation.welcome.WelcomeState
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel

class CodeFragment : BaseFragment<CodeState, CodeEvent>(R.layout.fragment_code) {
    override val vm : CodeViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentCodeBinding::bind)

    override fun renderState(state: CodeState) = with(binding) {
        when(state){
            is CodeState.CorrectCodeState -> {
            // TODO ПЕРЕХОД НА СТРАНИЦУ С ПЕРСОНАЛЬНЫМИ ДАННЫМИ
            }
            is CodeState.UncorrectCodeState -> {
                binding.codeInput.setBackgroundResource(R.drawable.error_rounded_border)
                binding.resendCode.setTextColor(resources.getColor(R.color.error,null))
                binding.error.visibility = View.VISIBLE
            }
            is CodeState.BeginCodeState -> {
                binding.codeInput.setBackgroundResource(R.drawable.rounded_border)
                binding.resendCode.setTextColor(resources.getColor(R.color.button,null))
                binding.error.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSendCode.setOnClickListener {
            vm.checkCode(binding.codeInput.text.toString())
        }

        binding.codeInput.addTextChangedListener {
            vm.overWriteCode()
        }

        binding.resendCode.setOnClickListener {
            // TODO ПОВТОРНАЯ ОТПРАВКА КОДА
        }
    }

    override fun handleEvent(event: CodeEvent): Unit = noEventsExpected()

}