package io.fasthome.fenestram_messenger.auth_impl.presentation.personality

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import coil.load
import coil.transform.CircleCropTransformation
import com.santalu.maskara.widget.MaskEditText
import io.fasthome.component.pick_file.PickFileComponentContract
import io.fasthome.component.pick_file.PickFileComponentParams
import io.fasthome.fenestram_messenger.auth_impl.R
import io.fasthome.fenestram_messenger.auth_impl.databinding.FragmentPersonalityBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.model.Bytes
import io.fasthome.fenestram_messenger.util.model.Bytes.Companion.BYTES_PER_MB
import io.fasthome.fenestram_messenger.util.setPrintableText

class PersonalityFragment :
    BaseFragment<PersonalityState, PersonalityEvent>(R.layout.fragment_personality) {


    private val pickImageFragment by registerFragment(
        componentFragmentContractInterface = PickFileComponentContract,
        paramsProvider = {
            PickFileComponentParams(
                mimeType = PickFileComponentParams.MimeType.Image(
                    compressToSize = Bytes(
                        BYTES_PER_MB
                    )
                )
            )
        }
    )

    override val vm: PersonalityViewModel by viewModel(
        getParamsInterface = PersonalityNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::pickImageFragment),
    )

    private val binding by fragmentViewBinding(FragmentPersonalityBinding::bind)

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
                    name = nameInput.includeEditText.text.toString(),
                    nickname = userNameInput.includeEditText.text.toString(),
                    birthday = birthdateInput.masked,
                    mail = mailInput.includeEditText.text.toString(),
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
                vm.fillData(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.NameKey
                )
            }

            userNameInput.includeEditText.setOnFocusChangeListener { v, hasFocus ->
                vm.fillData(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.UserNameKey
                )
            }

            birthdateInput.setOnFocusChangeListener { v, hasFocus ->
                vm.fillData(
                    (v as MaskEditText).masked,
                    hasFocus,
                    EditTextKey.BirthdateKey
                )
            }

            mailInput.includeEditText.setOnFocusChangeListener { v, hasFocus ->
                vm.fillData(
                    (v as EditText).text.toString(),
                    hasFocus,
                    EditTextKey.MailKey
                )
            }

            userPhoto.setOnClickListener {
                vm.onSelectPhotoClicked()
            }
        }

    }

    override fun renderState(state: PersonalityState): Unit = with(binding) {
        state.fieldsData.forEach { field ->
            when (field.key) {
                EditTextKey.NameKey -> nameInput.includeEditText
                EditTextKey.UserNameKey -> userNameInput.includeEditText
                EditTextKey.BirthdateKey -> birthdateInput
                EditTextKey.MailKey -> mailInput.includeEditText
            }.let { editText ->
                editText.setPrintableText(field.text)
                if (field.visibility) {
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_check,
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
                            setBackgroundResource(R.drawable.rounded_blue_button)
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
        }


        state.profileImageUrl?.let { url ->
            userPhoto.load(url) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_baseline_account_circle_24)
            }
        }
        state.avatarBitmap?.let { bitmap ->
            userPhoto.load(bitmap) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_baseline_account_circle_24)
            }
        }
    }

    override fun handleEvent(event: PersonalityEvent) {
        when (event) {
            is PersonalityEvent.LaunchGallery -> {
//                galleryLauncher.launch(getGalleryIntent())
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