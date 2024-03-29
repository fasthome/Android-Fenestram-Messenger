/**
 * Created by Dmitry Popov on 17.09.2022.
 */
package io.fasthome.component.personality_data

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import io.fasthome.component.R
import io.fasthome.component.databinding.FragmentPersonalityComponentBinding
import io.fasthome.fenestram_messenger.navigation.contract.InterfaceFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.custom_view.HooliDatePicker
import io.fasthome.fenestram_messenger.util.*
import io.fasthome.fenestram_messenger.util.android.RegexInputFilter

class PersonalityComponentFragment :
    BaseFragment<PersonalityState, PersonalityEvent>(R.layout.fragment_personality_component),
    InterfaceFragment<PersonalityInterface> {

    private val binding by fragmentViewBinding(FragmentPersonalityComponentBinding::bind)

    companion object {
        const val MAX_SYMBOLS = 15
        const val MAX_SYMBOLS_NICKNAME = 22
        const val MIN_SYMBOLS_NICKNAME = 2
        const val MAX_SYMBOLS_EMAIL = 30
    }

    override val vm: PersonalityComponentViewModel by viewModel(
        getParamsInterface = PersonalityComponentContract.getParams
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        labelBirthday.includeTextView.setText(R.string.common_birthday_label)
        labelMail.includeTextView.setText(R.string.common_email_label)
        labelName.includeTextView.setText(R.string.common_name_label)
        labelUserName.includeTextView.setText(R.string.common_user_name_label)
        nicknameInfo.text = getString(R.string.profile_nickname_info, MIN_SYMBOLS_NICKNAME)


        nameInput.filters += listOf(
            InputFilter.LengthFilter(MAX_SYMBOLS),
            SpaceInputFilter(REGEX_LETTERS_NICKNAME)
        )

        mailInput.filters += listOf(
            RegexInputFilter(REGEX_EMAIL_INPUT_FILTER, true),
            InputFilter.LengthFilter(MAX_SYMBOLS_EMAIL)
        )

        userNameInput.filters += listOf(
            SpaceInputFilter(REGEX_LETTERS_NICKNAME),
            InputFilter.LengthFilter(MAX_SYMBOLS_NICKNAME),
            SpacesToUnderscoresInputFilter()
        )

        nameInput.doAfterTextChanged {
            it?.let {
                vm.onFieldChanged(EditTextKey.UsernameKey, it.toString())
            }
        }
        mailInput.doAfterTextChanged {
            it?.let {
                vm.onFieldChanged(EditTextKey.MailKey, it.toString())
            }
        }
        userNameInput.doAfterTextChanged {
            if (it.toString().contains(' ')) {
                userNameInput.lastCharFocus()
            }
            it?.let {
                vm.onFieldChanged(EditTextKey.NicknameKey, userNameInput.text.toString())
            }
        }
        birthdateInput.doAfterTextChanged {
            it?.let {
                vm.onFieldChanged(EditTextKey.BirthdateKey, it.toString())
            }
        }
        HooliDatePicker(birthdateInput).registerDatePicker(childFragmentManager)
    }

    override fun onResume() {
        super.onResume()
        vm.invalidateState()
    }

    override fun renderState(state: PersonalityState) = with(binding) {
        state.fields.toList().forEach { field ->
            if (state.singleValidate.singleValidate) {
                vm.onFieldChanged(
                    field.first, getPrintableTextOrNull(field.second.text) ?: "",
                    singleValidate = true,
                    needValidate = state.visibilityIcon,
                )
            }
            when (field.first) {
                EditTextKey.UsernameKey -> {
                    nameInput.renderValidateField(
                        errorName.includeTextView,
                        field.second.error,
                        field.second.text,
                        acceptName,
                        field.second.visibilityIcon,
                        state.visibilityIcon
                    )
                }
                EditTextKey.MailKey -> {
                    mailInput.renderValidateField(
                        errorMail.includeTextView,
                        field.second.error,
                        field.second.text,
                        acceptMail,
                        field.second.visibilityIcon,
                        state.visibilityIcon
                    )
                }
                EditTextKey.BirthdateKey -> {
                    birthdateInput.renderValidateField(
                        errorBirthday.includeTextView,
                        field.second.error,
                        field.second.text,
                        acceptBirthday,
                        field.second.visibilityIcon,
                        state.visibilityIcon
                    )
                }
                EditTextKey.NicknameKey -> {
                    userNameInput.renderValidateField(
                        errorNickname.includeTextView,
                        field.second.error,
                        field.second.text,
                        acceptNickname,
                        field.second.visibilityIcon,
                        state.visibilityIcon
                    )
                }
            }
        }
    }

    override fun handleEvent(event: PersonalityEvent) = with(binding) {
        when (event) {
            is PersonalityEvent.RunEdit -> {
                nameInput.isVisible = event.edit
                labelName.includeTextView.isVisible = event.edit
                errorName.includeTextView.isVisible = event.edit
                nameInput.isEnabled = event.edit
                mailInput.isEnabled = event.edit
                birthdateInput.isEnabled = event.edit
                userNameInput.isEnabled = event.edit
                nicknameInfo.isVisible = event.edit
            }
            is PersonalityEvent.VisibleName -> {
                nameInput.isVisible = event.isVisible
                labelName.includeTextView.isVisible = event.isVisible
                errorName.includeTextView.isVisible = false
            }
        }
    }

    override fun getInterface(): PersonalityInterface = vm

    private fun View.renderValidateField(
        errorView: TextView,
        error: PrintableText?,
        text: PrintableText?,
        acceptIcon: ImageView,
        visibilityIcon: Boolean,
        visibilityIconsCommon: Boolean
    ) {
        (this as? TextView)?.apply {
            text?.let { setPrintableText(it) }
        }
        acceptIcon.isVisible = visibilityIcon && visibilityIconsCommon
        if (error == null) {
            errorView.isVisible = false
            this.setBackgroundResource(R.drawable.rounded_border)
        } else {
            error?.let { errorView.setPrintableText(it) }
            errorView.isVisible = true
            this.setBackgroundResource(R.drawable.error_rounded_border)
        }
    }
}