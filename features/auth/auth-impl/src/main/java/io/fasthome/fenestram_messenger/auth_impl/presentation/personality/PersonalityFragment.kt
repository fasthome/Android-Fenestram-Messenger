package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import coil.load
import coil.transform.CircleCropTransformation
import com.santalu.maskara.widget.MaskEditText
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentPersonalityBinding
import io.fasthome.fenestram_messenger.auth_impl.presentation.personality.model.PersonalData
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getGalleryIntent
import io.fasthome.fenestram_messenger.util.resultLauncher
import io.fasthome.fenestram_messenger.util.setPrintableText

class PersonalityFragment :
    BaseFragment<PersonalityState, PersonalityEvent>(R.layout.fragment_personality) {

    private val permissionInterface by registerFragment(PermissionComponentContract)
    override val vm: PersonalityViewModel by viewModel(
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    private val galleryLauncher = resultLauncher { result ->
        vm.onUpdatePhoto(result.data?.data)
    }


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
                )
                    buttonReady.apply {
                        setBackgroundResource(R.drawable.rounded_button)
                        isEnabled = true
                    }
                else
                    buttonReady.apply {
                        setBackgroundResource(R.drawable.rounded_gray_button)
                        isEnabled = false
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

        if (state.avatar != null)
            userPhoto.load(state.avatar) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_baseline_account_circle_24)
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            labelName.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_name_label))
            labelUserName.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_user_name_label))
            labelBirthday.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_birthday_label))
            labelMail.includeTextView.setPrintableText(PrintableText.StringResource(R.string.common_email_label))
            labelWelcomeStart.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.common_welcome_message_label_start))
            labelWelcomeEnd.includeWelcomeText.setPrintableText(PrintableText.StringResource(R.string.common_welcome_message_label_end))

            birthdateInput.inputType = InputType.TYPE_CLASS_DATETIME

            labelName.includeTextView.hint

            buttonReady.setOnClickListener {
                vm.checkPersonalData(
                    PersonalData(
                        nameInput.includeEditText.text.toString(),
                        userNameInput.includeEditText.text.toString(),
                        birthdateInput.masked,
                        mailInput.includeEditText.text.toString(),
                        "",
                        ""
                    )
                )
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
                vm.fillingEmail(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.MailKey
                )
            }

            userPhoto.setOnClickListener {
                vm.requestPermissionAndLoadPhoto()
            }
        }

    }

    override fun handleEvent(event: PersonalityEvent) {
        when (event) {
            is PersonalityEvent.LaunchGallery -> {
                galleryLauncher.launch(getGalleryIntent())
            }
        }
    }

    enum class EditTextKey {
        NameKey,
        UserNameKey,
        BirthdateKey,
        MailKey
    }
}