package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.databinding.FragmentContactAddBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.*
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.getPrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText


class ContactAddFragment :
    BaseFragment<ContactAddState, ContactAddEvent>(R.layout.fragment_contact_add) {

    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm: ContactAddViewModel by viewModel(
        getParamsInterface = ContactAddNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    private val binding: FragmentContactAddBinding by fragmentViewBinding(FragmentContactAddBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setOnButtonClickListener {
                vm.navigateBack()
            }
            contactAddInputSecondName.includeEditText.setBackgroundResource(R.drawable.bg_rounded_edittext)

            contactAddLabelFirstName.includeTextView.setPrintableText(PrintableText.StringResource(R.string.contact_add_first_name_label))

            contactAddLabelSecondName.includeTextView.setPrintableText(
                PrintableText.StringResource(
                    R.string.contact_add_second_name_label
                )
            )
            contactAddLabelNumber.includeTextView.setPrintableText(PrintableText.StringResource(R.string.contact_add_number_label))

            contactAddInputFirstName.includeEditText.hint =
                getPrintableText(PrintableText.StringResource(R.string.contact_add_first_name_hint))
            contactAddInputSecondName.includeEditText.hint =
                getPrintableText(PrintableText.StringResource(R.string.contact_add_second_name_hint))

            contactAddInvalidFirstName.includeTextInvalid.run {
                setPrintableText(
                    PrintableText.StringResource(
                        R.string.contact_add_name_invalid
                    )
                )
                visibility = View.GONE
            }

            contactAddInputFirstName.includeEditText.addTextChangedListener {
                vm.onNameTextChanged(
                    contactAddInputFirstName.includeEditText.text.toString(),
                    contactAddInputNumber.unMasked
                )
            }

            contactAddInputNumber.addTextChangedListener {
                vm.onNumberTextChanged(
                    contactAddInputFirstName.includeEditText.text.toString(),
                    contactAddInputNumber.unMasked
                )
            }

            contactAddButtonReady.setOnClickListener {
                vm.checkAndWriteContact(
                    contactAddInputFirstName.includeEditText.text.toString(),
                    contactAddInputSecondName.includeEditText.text.toString(),
                    contactAddInputNumber.unMasked
                )
            }
        }
    }

    override fun renderState(state: ContactAddState) = with(binding) {
        when (state.contactAddStatus) {
            EditTextStatus.NameIdle -> {
                renderNameIdle()
                contactAddButtonReady.isEnabled = false
            }

            EditTextStatus.NumberIdle -> {
                renderNumberIdle()
                contactAddButtonReady.isEnabled = false
            }

            EditTextStatus.NameFilledAndNumberCorrect -> {
                contactAddButtonReady.isEnabled = true
                renderNameIdle()
                renderNumberIdle()
            }

            EditTextStatus.NameEmptyAndNumberEmpty -> {
                renderNameEmpty()
                renderNumberEmpty()
            }

            EditTextStatus.NameEmptyAndNumberIncorrect -> {
                renderNameEmpty()
                renderNumberIncorrect()
            }

            EditTextStatus.NameEmptyAndNumberCorrect -> {
                renderNameEmpty()
            }

            EditTextStatus.NameFilledAndNumberEmpty -> {
                renderNumberEmpty()
            }

            EditTextStatus.NameFilledAndNumberIncorrect -> {
                renderNumberIncorrect()
            }
        }
    }

    override fun handleEvent(event: ContactAddEvent) = noEventsExpected()

    private fun renderNameIdle() = with(binding) {
        contactAddInputFirstName.includeEditText.setBackgroundResource(R.drawable.bg_rounded_edittext)
        contactAddInvalidFirstName.includeTextInvalid.visibility = View.GONE
        contactAddLabelFirstName.includeTextView.setTextColor(
            ContextCompat.getColor(
                contactAddLabelFirstName.includeTextView.context,
                R.color.gray
            )
        )
    }

    private fun renderNumberIdle() = with(binding) {
        contactAddInputNumber.setBackgroundResource(R.drawable.bg_rounded_edittext)
        contactAddInvalidNumber.includeTextInvalid.visibility = View.GONE
        contactAddLabelNumber.includeTextView.setTextColor(
            ContextCompat.getColor(
                contactAddLabelNumber.includeTextView.context,
                R.color.gray
            )
        )
    }

    private fun renderNameEmpty() = with(binding) {
        contactAddInputFirstName.includeEditText.setBackgroundResource(R.drawable.bg_rounded_edittext_error)
        contactAddInvalidFirstName.includeTextInvalid.visibility = View.VISIBLE
        contactAddLabelFirstName.includeTextView.setTextColor(
            ContextCompat.getColor(
                contactAddLabelFirstName.includeTextView.context,
                R.color.red
            )
        )
    }

    private fun renderNumberEmpty() = with(binding) {
        contactAddInputNumber.setBackgroundResource(R.drawable.bg_rounded_edittext_error)
        contactAddInvalidNumber.includeTextInvalid.run {
            setPrintableText(
                PrintableText.StringResource(
                    R.string.contact_add_number_empty
                )
            )
            visibility = View.VISIBLE
        }
        contactAddLabelNumber.includeTextView.setTextColor(
            ContextCompat.getColor(
                contactAddLabelNumber.includeTextView.context,
                R.color.red
            )
        )
    }

    private fun renderNumberIncorrect() = with(binding) {
        contactAddInputNumber.setBackgroundResource(R.drawable.bg_rounded_edittext_error)
        contactAddInvalidNumber.includeTextInvalid.run {
            setPrintableText(
                PrintableText.StringResource(
                    R.string.contact_add_number_invalid
                )
            )
            visibility = View.VISIBLE
        }
        contactAddLabelNumber.includeTextView.setTextColor(
            ContextCompat.getColor(
                contactAddLabelNumber.includeTextView.context,
                R.color.red
            )
        )
    }


    enum class EditTextStatus {
        NameIdle,
        NumberIdle,
        NameEmptyAndNumberEmpty,
        NameEmptyAndNumberIncorrect,
        NameEmptyAndNumberCorrect,
        NameFilledAndNumberEmpty,
        NameFilledAndNumberIncorrect,
        NameFilledAndNumberCorrect
    }

}
