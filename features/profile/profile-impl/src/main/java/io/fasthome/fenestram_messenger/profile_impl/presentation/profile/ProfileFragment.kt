/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.profile_impl.presentation.profile

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import coil.load
import coil.transform.CircleCropTransformation
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.profile_impl.R
import io.fasthome.fenestram_messenger.profile_impl.databinding.FragmentProfileBinding
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class ProfileFragment : BaseFragment<ProfileState, ProfileEvent>(R.layout.fragment_profile) {

    override val vm: ProfileViewModel by viewModel()

    private val binding by fragmentViewBinding(FragmentProfileBinding::bind)

    override fun renderState(state: ProfileState): Unit = with(binding) {
        when (state.key) {
            EditTextKey.UserNameKey -> nickContainer.includeEditText
            EditTextKey.BirthdateKey -> hbDayContainer
            EditTextKey.MailKey -> emailContainer.includeEditText
            else -> null
        }?.let { editText ->
            if (state.visibility) {


                if (arrayOf(
                        nickContainer.includeEditText,
                        hbDayContainer,
                        emailContainer.includeEditText
                    ).count { it.compoundDrawablesRelative[2] != null } == 4
                ) {
                    bDone.apply {
                        setBackgroundResource(R.drawable.rounded_blue_button)
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

                bDone.apply {
                    setBackgroundResource(R.drawable.rounded_gray_button)
                    isEnabled = false
                }
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            labelNicknameU.includeTextView.setPrintableText(PrintableText.StringResource(R.string.auth_user_name_label))
            labelHBDay.includeTextView.setPrintableText(PrintableText.StringResource(R.string.auth_birthday_label))
            labelEmail.includeTextView.setPrintableText(PrintableText.StringResource(R.string.auth_email_label))


            hbDayContainer.inputType = InputType.TYPE_CLASS_DATETIME

            bDone.setOnClickListener {
                vm.checkPersonalData()
            }

            bCancel.setOnClickListener {
                vm.skipPersonalData()
            }


            nickContainer.includeEditText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    v.clearFocus()
                return@setOnEditorActionListener false
            }

            hbDayContainer.setOnEditActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    v.clearFocus()
                return@setOnEditorActionListener false
            }

            emailContainer.includeEditText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    v.clearFocus()
                return@setOnEditorActionListener false
            }



            nickContainer.includeEditText.setOnFocusChangeListener { v, hasFocus ->
                vm.fillingPersonalData(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.UserNameKey
                )
            }

            hbDayContainer.setOnFocusChangeListener { v, hasFocus ->
                vm.fillingBirthdate(
                    (v as MaskEditText).masked,
                    hasFocus,
                    EditTextKey.BirthdateKey
                )
            }

            emailContainer.includeEditText.setOnFocusChangeListener { v, hasFocus ->
                vm.fillingPersonalData(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.MailKey
                )
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.ivAvatar.load("https://motor.ru/thumb/1500x0/filters:quality(75):no_upscale()/imgs/2021/06/18/14/4723617/344b418d34027278297fab26541cd61da878a163.jpg") {
            transformations(CircleCropTransformation())
        }
    }


    override fun handleEvent(event: ProfileEvent) = noEventsExpected()

    enum class EditTextKey {
        NameKey,
        UserNameKey,
        BirthdateKey,
        MailKey
    }


}