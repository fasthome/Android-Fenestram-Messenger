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

    override fun renderState(state: PersonalityState): Unit = with(binding) {
        when (state.key) {
            EditTextKey.NameKey -> nameInput.includeEditText
            EditTextKey.UserNameKey -> userNameInput.includeEditText
            EditTextKey.BirthdateKey -> birthdateInput
            EditTextKey.MailKey -> mailInput.includeEditText
            else -> null
        }?.let { editText ->
            if (state.visibility) {
                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
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
                    buttonReady.apply {
                        setBackgroundResource(R.drawable.rounded_button)
                        isEnabled = true
                    }
                }
            } else {
                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    0,
                    0
                )

                buttonReady.apply {
                    setBackgroundResource(R.drawable.rounded_gray_button)
                    isEnabled = false
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            labelName.includeTextView.setPrintableText(PrintableText.StringResource(R.string.auth_name_label))
            labelUserName.includeTextView.setPrintableText(PrintableText.StringResource(R.string.auth_user_name_label))
            labelBirthday.includeTextView.setPrintableText(PrintableText.StringResource(R.string.auth_birthday_label))
            labelMail.includeTextView.setPrintableText(PrintableText.StringResource(R.string.auth_email_label))
            labelWelcomeStart.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.auth_welcome_message_label_start))
            labelWelcomeEnd.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.auth_welcome_message_label_end))

            birthdateInput.inputType = InputType.TYPE_CLASS_DATETIME

            buttonReady.setOnClickListener {
                vm.checkPersonalData()
            }

            labelSkip.setOnClickListener {
                vm.skipPersonalData()
            }

            nameInput.includeEditText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    v.clearFocus()
                return@setOnEditorActionListener false
            }

            userNameInput.includeEditText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    v.clearFocus()
                return@setOnEditorActionListener false
            }

            birthdateInput.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    v.clearFocus()
                return@setOnEditorActionListener false
            }

            mailInput.includeEditText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    v.clearFocus()
                return@setOnEditorActionListener false
            }

            nameInput.includeEditText.setOnFocusChangeListener { v, hasFocus ->
                vm.fillingPersonalData(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.NameKey
                )
            }

            userNameInput.includeEditText.setOnFocusChangeListener { v, hasFocus ->
                vm.fillingPersonalData(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.UserNameKey
                )
            }

            birthdateInput.setOnFocusChangeListener { v, hasFocus ->
                vm.fillingBirthdate(
                    (v as MaskEditText).masked,
                    hasFocus,
                    EditTextKey.BirthdateKey
                )
            }

            mailInput.includeEditText.setOnFocusChangeListener { v, hasFocus ->
                vm.fillingPersonalData(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.MailKey
                )
            }

    }

    override fun handleEvent(event: PersonalityEvent): Unit = noEventsExpected()

    enum class EditTextKey {
        NameKey,
        UserNameKey,
        BirthdateKey,
        MailKey
    }
}