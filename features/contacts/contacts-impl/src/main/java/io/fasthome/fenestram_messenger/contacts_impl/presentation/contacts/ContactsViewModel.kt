/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.domain.logic.ContactsInteractor
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.ContactAddNavigationContract
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.mapper.ContactsMapper
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.core.exceptions.EmptyResponseException
import io.fasthome.fenestram_messenger.core.exceptions.EmptySearchException
import io.fasthome.fenestram_messenger.core.exceptions.PermissionDeniedException
import io.fasthome.fenestram_messenger.messenger_api.MessengerFeature
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.mvi.Message
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
    private val contactsInteractor: ContactsInteractor,
    private val messengerFeature: MessengerFeature
) : BaseViewModel<ContactsState, ContactsEvent>(router, requestParams) {

    private val addContactLauncher = registerScreen(ContactAddNavigationContract) { result ->
        when (result) {
            is ContactAddNavigationContract.ContactAddResult.Success -> Unit
            is ContactAddNavigationContract.ContactAddResult.Canceled -> showMessage(Message.PopUp(result.message))
        }
    }

    private var originalContactsViewItem = mutableListOf<ContactsViewItem>()

    private val conversationLauncher =
        registerScreen(messengerFeature.conversationNavigationContract) { }

    @SuppressLint("MissingPermission")
    fun requestPermissionAndLoadContacts() {
        viewModelScope.launch {
            updateState { state ->
                state.copy(loadingState = LoadingState.Loading)
            }
            val permissionGranted = permissionInterface.request(Manifest.permission.READ_CONTACTS)
            val selfUserPhone = contactsInteractor.getSelfUserPhone()
            if (permissionGranted) {
                when (val result = contactsInteractor.getContactsAndUploadContacts()) {
                    is CallResult.Error -> updateState { state ->
                        state.copy(
                            loadingState = LoadingState.Error(
                                error = errorConverter.convert(result.error),
                                throwable = result.error
                            )
                        )
                    }
                    is CallResult.Success -> updateState { state ->
                        originalContactsViewItem =
                            ContactsMapper.contactsListToViewList(result.data, selfUserPhone)
                                .toMutableList()
                        if (originalContactsViewItem.isEmpty()) {
                            state.copy(
                                loadingState = LoadingState.Error(
                                    error = ErrorInfo.createEmpty(),
                                    throwable = EmptyResponseException()
                                )
                            )
                        } else {
                            state.copy(loadingState = LoadingState.Success(data = originalContactsViewItem))
                        }
                    }
                }
            } else {
                updateState {
                    ContactsState(
                        LoadingState.Error(
                            error = ErrorInfo.createEmpty(),
                            throwable = PermissionDeniedException()
                        )
                    )
                }
            }
        }
    }

    override fun createInitialState(): ContactsState {
        return ContactsState(LoadingState.None)
    }

    fun addContact() {
        addContactLauncher.launch(ContactAddNavigationContract.Params(null, null))
    }

    fun filterContacts(query: String) {
        val filteredContacts = if (query.isEmpty()) {
            originalContactsViewItem
        } else {
            originalContactsViewItem.filter {
                if (it !is ContactsViewItem.Header) {
                    getPrintableRawText(it.name).contains(query.trim(), true)
                } else {
                    false
                }
            }
        }
        updateState { state ->
            state.copy(
                loadingState = LoadingState.Success(filteredContacts)
            )
        }
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

    fun onContactClicked(contactsViewItem: ContactsViewItem) {
        if (contactsViewItem is ContactsViewItem.Api) {
            conversationLauncher.launch(
                MessengerFeature.Params(
                    userIds = listOf(contactsViewItem.userId),
                    chatName = getPrintableRawText(contactsViewItem.name),
                    avatar = contactsViewItem.avatar,
                    isGroup = false
                )
            )
        }
    }

    fun onOtherError(throwable: Throwable) {
        onError(
            showErrorType = ShowErrorType.Dialog,
            throwable = throwable,
            onRetryClick = { requestPermissionAndLoadContacts() })
    }

}