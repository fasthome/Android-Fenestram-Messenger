package io.fasthome.fenestram_messenger.auth_ad_impl.presentation.login

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.auth_ad_impl.R
import io.fasthome.fenestram_messenger.auth_ad_impl.databinding.FragmentLoginBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.getPrintableText
import io.fasthome.fenestram_messenger.util.onClick


class LoginFragment : BaseFragment<LoginState, LoginEvent>(R.layout.fragment_login) {

    override val vm: LoginViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

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

    override fun syncTheme(appTheme: Theme): Unit = with(binding) {
        appTheme.context = requireContext()
        linearLayout.background = appTheme.backgroundGeometry()
        DrawableCompat.setTint(
            DrawableCompat.wrap(logoIv.drawable),
            appTheme.logoColor()
        )
        loginInput.setTextColor(appTheme.text0Color())
        passwordInput.setTextColor(appTheme.text0Color())
        loginInputLayout.apply {
            boxBackgroundColor = appTheme.boxBackgroundColor()
            setBoxStrokeColorStateList(appTheme.boxStrokeColor())
        }
        passwordInputLayout.apply {
            boxBackgroundColor = appTheme.boxBackgroundColor()
            setBoxStrokeColorStateList(appTheme.boxStrokeColor())
            setEndIconTintList(appTheme.endIconTint())
        }
        loginButton.setBackgroundResource(appTheme.buttonDrawableRes())
    }

    override fun renderState(state: LoginState) = with(binding) {
        loginButton.isEnabled = state.loginButtonEnabled
        loginInputLayout.error = getPrintableText(state.loginErrorMessage)
        passwordInputLayout.error = getPrintableText(state.passwordErrorMessage)
    }

    override fun handleEvent(event: LoginEvent) = noEventsExpected()

}