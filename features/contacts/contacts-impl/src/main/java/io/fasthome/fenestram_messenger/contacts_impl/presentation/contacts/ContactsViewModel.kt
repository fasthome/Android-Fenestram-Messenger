/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper.DepartmentsMapper
import io.fasthome.fenestram_messenger.contacts_impl.domain.logic.DepartmentInteractor
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.DepartmentViewItem
import io.fasthome.fenestram_messenger.core.exceptions.EmptySearchException
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.ShowErrorType
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import io.fasthome.fenestram_messenger.uikit.R
import io.fasthome.fenestram_messenger.util.*
import kotlinx.coroutines.launch

class ContactsViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
    private val departmentInteractor: DepartmentInteractor,
    private val departmentsMapper: DepartmentsMapper,
    private val messengerFeature: MessengerFeature,
) : BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

    private var originalViewItem = listOf<DepartmentViewItem>()

    private val conversationLauncher =
        registerScreen(messengerFeature.conversationNavigationContract) { }

    override fun createInitialState(): ContactsState {
        return ContactsState(LoadingState.None)
    }

    fun filterContacts(query: String) {
        val filteredContacts = if (query.isEmpty()) {
            originalViewItem
        } else {
            emptyList()
            /*
            val divisions = originalViewItem.filterIsInstance<DepartmentViewItem.Division>().toMutableList()
            val searchItems = mutableListOf<DepartmentViewItem.Division>()
            divisions.forEach {

            }

            divisions.filter { division ->
                division.employee.filter {
                    getPrintableRawText(it.name).contains(query.trim(), true)
                }
            }*/
        }
       updateState { state ->
            state.copy(
                loadingState = LoadingState.Success(filteredContacts)
            )
        } // TODO: !!! Make search
        if (filteredContacts.isEmpty()) {
            updateState { state ->
                state.copy(
                    loadingState = LoadingState.Error(
                        error = ErrorInfo(
                            PrintableText.EMPTY,
                            PrintableText.StringResource(R.string.contacts_empty_view, query)
                        ),
                        throwable = EmptySearchException()
                    )
                )
            }
        }
    }

    fun loadDepartments() {
        viewModelScope.launch {
            updateState { state ->
                state.copy(loadingState = LoadingState.Loading)
            }
            val result = departmentInteractor.getDepartments()
            when(result) {
                is CallResult.Success -> {
                    val items = departmentsMapper.departmentModelToViewItem(result.data)
                    updateState { state ->
                        originalViewItem = items
                        state.copy(
                            loadingState = LoadingState.Success(data = items),
                        )
                    }
                }
                is CallResult.Error -> {
                    updateState { state ->
                        state.copy(loadingState = LoadingState.None)
                    }
                }
            }
        }
    }

    fun onContactClicked(contactsViewItem: ContactsViewItem) {
        conversationLauncher.launch(
            MessengerFeature.Params(
                userIds = listOf(contactsViewItem.userId),
                chatName = getPrintableRawText(contactsViewItem.name),
                avatar = contactsViewItem.avatar,
                isGroup = false
            )
        )
    }

    fun onOtherError(throwable: Throwable) {
        onError(
            showErrorType = ShowErrorType.Dialog,
            throwable = throwable,
            onRetryClick = { loadDepartments() }
        )
    }

}