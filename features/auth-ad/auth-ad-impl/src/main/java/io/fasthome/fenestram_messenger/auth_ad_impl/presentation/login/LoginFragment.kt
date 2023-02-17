package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import io.fasthome.fenestram_messenger.auth_ad_impl.R
import io.fasthome.fenestram_messenger.auth_ad_impl.databinding.FragmentLoginBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.onClick


class LoginFragment : BaseFragment<LoginState, LoginEvent>(R.layout.fragment_login) {

    override val vm: LoginViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        loginButton.onClick {
            vm.checkLoginData(loginInput.text.toString().trim(), passwordInput.text.toString())
        }

    }

    override fun renderState(state: LoginState) = with(binding) {

    }

    override fun handleEvent(event: LoginEvent) = noEventsExpected()

}