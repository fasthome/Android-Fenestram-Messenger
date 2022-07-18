package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import android.graphics.drawable.GradientDrawable
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
        binding.contactAddAppbar.setNavigationOnClickListener {
            vm.navigateBack()
        }
        with(binding) {

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
                vm.onNameChanged(contactAddInputFirstName.includeEditText.text.toString())
            }

            contactAddInputNumber.addTextChangedListener {
                vm.onNumberChanged(contactAddInputNumber.unMasked)
            }

            contactAddInputFirstName.includeEditText.setOnFocusChangeListener { _, _ ->
                vm.onNameChanged(contactAddInputFirstName.includeEditText.text.toString())
            }

            contactAddInputNumber.setOnFocusChangeListener { _, _ ->
                vm.onNumberChanged(contactAddInputNumber.unMasked)
            }

            contactAddButtonReady.setOnClickListener {
                vm.writeContact()
            }
        }
    }

    override fun renderState(state: ContactAddState) {
        if (state.idle)
            return

        with(binding) {
            if (state.isNameFilled && state.isNumberCorrect) {
                contactAddButtonReady.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.blue
                    )
                )
                contactAddButtonReady.isEnabled = true
            } else {
                contactAddButtonReady.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.appbar_color
                    )
                )
                contactAddButtonReady.isEnabled = false
            }

            if (state.isNameFilled) {
                (contactAddInputFirstName.includeEditText.background as GradientDrawable).setStroke(
                    3,
                    ContextCompat.getColor(context!!, R.color.manatee)
                )
                contactAddInvalidFirstName.includeTextInvalid.visibility = View.GONE
                contactAddLabelFirstName.includeTextView.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.gray
                    )
                )
            } else {
                (contactAddInputFirstName.includeEditText.background as GradientDrawable).setStroke(
                    3,
                    ContextCompat.getColor(context!!, R.color.red)
                )
                contactAddInvalidFirstName.includeTextInvalid.visibility = View.VISIBLE
                contactAddLabelFirstName.includeTextView.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.red
                    )
                )
            }

            when {
                state.isNumberEmpty -> {
                    (contactAddInputNumber.background as GradientDrawable).setStroke(
                        3,
                        ContextCompat.getColor(context!!, R.color.red)
                    )
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
                            context!!,
                            R.color.red
                        )
                    )
                }

                state.isNumberCorrect -> {
                    (contactAddInputNumber.background as GradientDrawable).setStroke(
                        3,
                        ContextCompat.getColor(context!!, R.color.manatee)
                    )
                    contactAddInvalidNumber.includeTextInvalid.visibility = View.GONE
                    contactAddLabelNumber.includeTextView.setTextColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.gray
                        )
                    )
                }

                else -> {
                    (contactAddInputNumber.background as GradientDrawable).setStroke(
                        3,
                        ContextCompat.getColor(context!!, R.color.red)
                    )
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
                            context!!,
                            R.color.red
                        )
                    )
                }
            }
        }

    }

    override fun handleEvent(event: ContactAddEvent) = noEventsExpected()

}
