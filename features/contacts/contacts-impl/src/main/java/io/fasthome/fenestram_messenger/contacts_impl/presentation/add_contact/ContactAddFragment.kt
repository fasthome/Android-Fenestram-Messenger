package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import android.os.Bundle
import android.view.View
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.databinding.FragmentContactAddBinding
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.*
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setPrintableText

class ContactAddFragment : BaseFragment<ContactAddState, ContactAddEvent>(R.layout.fragment_contact_add) {

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
            contactAddLabelSecondName.includeTextView.setPrintableText(PrintableText.StringResource(R.string.contact_add_second_name_label))
            contactAddLabelNumber.includeTextView.setPrintableText(PrintableText.StringResource(R.string.contact_add_number_label))

            contactAddInvalidFirstName.includeTextInvalid.setPrintableText(PrintableText.StringResource(R.string.contact_add_name_invalid))
        }
    }

    override fun renderState(state: ContactAddState) = nothingToRender()

    override fun handleEvent(event: ContactAddEvent) = noEventsExpected()

}