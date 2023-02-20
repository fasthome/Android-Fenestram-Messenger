package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.auth_ad_impl.R
import io.fasthome.fenestram_messenger.auth_ad_impl.databinding.FragmentLoginBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.getPrintableText
import io.fasthome.fenestram_messenger.util.onClick


class LoginFragment : BaseFragment<LoginState, LoginEvent>(R.layout.fragment_login) {

    override val vm: LoginViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        animateKeyboard(listOf(loginButton), listOf(loginInput, passwordInput))

        loginInput.addTextChangedListener {
            vm.onTextChanged(it.toString().trim(), passwordInput.text.toString(), true)
        }
        loginInputLayout.children.forEach {
            it.setPadding(0, it.paddingTop, 0, it.paddingRight)
        }

        passwordInput.addTextChangedListener {
            vm.onTextChanged(loginInput.text.toString().trim(), it.toString(), false)
        }
        passwordInputLayout.children.forEach {
            it.setPadding(0, it.paddingTop, 0, it.paddingRight)
        }

        loginButton.onClick {
            vm.checkLoginData(loginInput.text.toString().trim(), passwordInput.text.toString())
        }
    }

    override fun renderState(state: LoginState) = with(binding) {
        loginButton.isEnabled = state.loginButtonEnabled
        loginButton.animateAlpha(state.loginButtonAlpha)
        loginInputLayout.error = getPrintableText(state.loginErrorMessage)
        passwordInputLayout.error = getPrintableText(state.passwordErrorMessage)
    }

    override fun handleEvent(event: LoginEvent) = noEventsExpected()

}