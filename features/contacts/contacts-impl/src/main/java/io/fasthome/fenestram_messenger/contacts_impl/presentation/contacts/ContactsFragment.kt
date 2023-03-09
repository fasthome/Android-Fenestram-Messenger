/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import io.fasthome.component.permission.PermissionComponentContract
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.databinding.FragmentContactsBinding
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.adapter.DepartmentAdapter
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.DepartmentViewItem
import io.fasthome.fenestram_messenger.core.exceptions.EmptyResponseException
import io.fasthome.fenestram_messenger.core.exceptions.EmptySearchException
import io.fasthome.fenestram_messenger.presentation.base.ui.BaseFragment
import io.fasthome.fenestram_messenger.presentation.base.ui.registerFragment
import io.fasthome.fenestram_messenger.presentation.base.util.InterfaceFragmentRegistrator
import io.fasthome.fenestram_messenger.presentation.base.util.fragmentViewBinding
import io.fasthome.fenestram_messenger.presentation.base.util.noEventsExpected
import io.fasthome.fenestram_messenger.presentation.base.util.viewModel
import io.fasthome.fenestram_messenger.uikit.theme.Theme
import io.fasthome.fenestram_messenger.util.ErrorInfo
import io.fasthome.fenestram_messenger.util.renderLoadingState
import io.fasthome.fenestram_messenger.util.setPrintableText


class ContactsFragment : BaseFragment<ContactsState, ContactsEvent>(R.layout.fragment_contacts) {

    private val permissionInterface by registerFragment(PermissionComponentContract)

    override val vm: ContactsViewModel by viewModel(
        getParamsInterface = ContactsNavigationContract.getParams,
        interfaceFragmentRegistrator = InterfaceFragmentRegistrator()
            .register(::permissionInterface)
    )

    private val binding: FragmentContactsBinding by fragmentViewBinding(FragmentContactsBinding::bind)

    private val departmentAdapter = DepartmentAdapter(
        onContactClick = { contact->
        vm.onContactClicked(contact)
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        with(binding) {
            rvDepartment.adapter = departmentAdapter

            binding.contactsEt.doOnTextChanged { text, start, before, count ->
                vm.filterContacts(text.toString())
            }
        }
        vm.loadDepartments()
    }

    override fun syncTheme(appTheme: Theme) {
        appTheme.context = requireActivity().applicationContext
        binding.contactsEt.background = appTheme.bgStroke2_8dp()
        binding.contactsHeader.setTextColor(appTheme.text0Color())
        binding.bgGeometry.background = appTheme.backgroundGeometry()
    }

    override fun renderState(state: ContactsState) = with(binding) {
        errorContainer.isVisible = false
        emptyContainer.isVisible = false

        renderLoadingState(
            loadingState = state.loadingState,
            progressContainer = contactsProgressBar,
            contentContainer = null,
            renderData = {
                departmentAdapter.items = it.map { depViewItem ->
                    if (depViewItem is DepartmentViewItem.Division) depViewItem.copy(textColor = getTheme().text0Color()) else depViewItem
                }
            },
            renderError = { errorInfo, throwable ->
                renderError(errorInfo, throwable)
            }
        )
    }

    override fun handleEvent(event: ContactsEvent) = noEventsExpected()

    private fun renderError(errorInfo: ErrorInfo, throwable: Throwable) = with(binding) {
        when (throwable) {
            is EmptyResponseException -> {
                errorContainer.isVisible = true
            }
            is EmptySearchException -> {
                emptyContainer.isVisible = true
                contactsEmptySearchText.setPrintableText(errorInfo.description)
            }
            else -> {
                vm.onOtherError(throwable)
            }
        }
    }


}