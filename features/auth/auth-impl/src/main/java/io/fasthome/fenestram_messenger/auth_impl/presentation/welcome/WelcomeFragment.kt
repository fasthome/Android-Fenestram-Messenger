package io.fasthome.fenestram_messenger.auth_impl.presentation.welcome

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentWelcomeBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil


class WelcomeFragment : BaseFragment<WelcomeState, WelcomeEvent>(R.layout.fragment_welcome) {

    override val vm: WelcomeViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentWelcomeBinding::bind)

    private lateinit var phoneNumberUtil: PhoneNumberUtil

    override fun renderState(state: WelcomeState): Unit = with(binding) {
        binding.countryName.text = state.country
        if (state.error)
            phoneInput.setBackgroundResource(R.drawable.error_rounded_border)
        else
            phoneInput.setBackgroundResource(R.drawable.rounded_border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phoneNumberUtil = PhoneNumberUtil.createInstance(requireContext())

        with(binding) {

            appName.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.common_fenestram_label))

            val phoneNumberInputManager =
                PhoneNumberInputManager(phoneNumberUtil, binding.phoneInput) {
                    vm.updateCountry(it)
                }

            buttonSendCode.setOnClickListener {
                val number = binding.phoneInput.text.toString()
                val isValid = phoneNumberInputManager.validateNumber(number)
                vm.checkPhoneNumber(
                    number.filter { it.isDigit() },
                    isValid
                )
            }

            with(phoneInput) {
                addTextChangedListener(phoneNumberInputManager)
                addTextChangedListener(PhoneNumberFormattingTextWatcher())
                addTextChangedListener {
                    vm.overWritePhoneNumber()
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return super.onBackPressed()
    }

    override fun handleEvent(event: WelcomeEvent): Unit = noEventsExpected()
}