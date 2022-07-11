package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.santalu.maskara.widget.MaskEditText
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentPersonalityBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class PersonalityFragment :
    BaseFragment<PersonalityState, PersonalityEvent>(R.layout.fragment_personality) {
    override val vm: PersonalityViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentPersonalityBinding::bind)

    override fun renderState(state: PersonalityState) = with(binding) {
        val editTextFilled = when (state.key) {
            nameKey -> nameInput.includeEditText
            userNameKey -> userNameInput.includeEditText
            birthdateKey -> birthdateInput
            mailKey -> mailInput.includeEditText
            else -> null
        }

        if (editTextFilled != null) {
            if (state.visibility) {
                editTextFilled.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_baseline_check_24,
                    0
                )

                if (arrayOf(
                        nameInput.includeEditText,
                        userNameInput.includeEditText,
                        birthdateInput,
                        mailInput.includeEditText
                    ).count { it.compoundDrawablesRelative[2] != null } == 4
                ) {
                    buttonReady.setBackgroundResource(R.drawable.rounded_button)
                    buttonReady.isEnabled = true
                }
            } else {
                editTextFilled.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
                )

                buttonReady.setBackgroundResource(R.drawable.rounded_gray_button)
                buttonReady.isEnabled = false
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.labelName.includeTextView.setPrintableText(PrintableText.StringResource(R.string.name_label))
        binding.labelUserName.includeTextView.setPrintableText(PrintableText.StringResource(R.string.user_name_label))
        binding.labelBirthday.includeTextView.setPrintableText(PrintableText.StringResource(R.string.birthday_label))
        binding.labelMail.includeTextView.setPrintableText(PrintableText.StringResource(R.string.email_label))
        binding.labelWelcomeStart.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.welcome_message_label_start))
        binding.labelWelcomeEnd.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.welcome_message_label_end))

        binding.birthdateInput.inputType = InputType.TYPE_CLASS_DATETIME

        binding.buttonReady.setOnClickListener {
            vm.checkPersonalData()
        }

        binding.labelSkip.setOnClickListener {
            vm.skipPersonalData()
        }

        binding.nameInput.includeEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                v.clearFocus()
            return@setOnEditorActionListener false
        }

        binding.userNameInput.includeEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                v.clearFocus()
            return@setOnEditorActionListener false
        }

        binding.birthdateInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                v.clearFocus()
            return@setOnEditorActionListener false
        }

        binding.mailInput.includeEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE)
                v.clearFocus()
            return@setOnEditorActionListener false
        }

        binding.nameInput.includeEditText.setOnFocusChangeListener { v, hasFocus ->
            vm.fillingPersonalData((v as EditText).text.toString(), hasFocus, nameKey)
        }

        binding.userNameInput.includeEditText.setOnFocusChangeListener { v, hasFocus ->
            vm.fillingPersonalData((v as EditText).text.toString(), hasFocus, userNameKey)
        }

        binding.birthdateInput.setOnFocusChangeListener { v, hasFocus ->
            vm.fillingBirthdate((v as MaskEditText).masked, hasFocus, birthdateKey)
        }

        binding.mailInput.includeEditText.setOnFocusChangeListener { v, hasFocus ->
            vm.fillingPersonalData((v as EditText).text.toString(), hasFocus, mailKey)
        }

    }

    override fun handleEvent(event: PersonalityEvent): Unit = noEventsExpected()

    companion object {
        const val nameKey = "Name"
        const val userNameKey = "UserName"
        const val birthdateKey = "Birthdate"
        const val mailKey = "Mail"
    }
}