package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.data.ContactsLoader
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ContactAddViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
    private val contactsLoader: ContactsLoader,
    private val params: ContactAddNavigationContract.Params
) : BaseViewModel<ContactAddState, ContactAddEvent>(router, requestParams) {

    init {
        requestPermissionToWriteContacts()
    }

    private val contactsLoaderScope = CoroutineScope(Job() + Dispatchers.IO)

    private fun requestPermissionToWriteContacts() {
        viewModelScope.launch {
            val permissionGranted = permissionInterface.request(Manifest.permission.WRITE_CONTACTS)

            if (!permissionGranted) {
                router.exit()
            }

        }
    }

    override fun createInitialState(): ContactAddState {
        return if (params.name != null && params.phone != null)
            ContactAddState.ContactAutoFillStatus(params.name, params.phone)
        else
            ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameIdle)
    }

    fun navigateBack() {
        router.exit()
    }

    fun onNameTextChanged(firstName: String, isValid: Boolean) {
        when {
            firstName.trim().isEmpty() -> updateState {
                ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameEmptyAndNumberCorrect)
            }
            firstName.isNotEmpty() && isValid -> updateState { ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameFilledAndNumberCorrect) }
            else -> updateState { ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameIdle) }
        }
    }

    fun onNumberTextChanged(firstName: String, isValid: Boolean) {
        if (firstName.isNotEmpty() && isValid) {
            updateState { ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameFilledAndNumberCorrect) }
        } else {
            updateState { ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NumberIdle) }
        }
    }

    fun checkAndWriteContact(firstName: String, secondName: String, phoneNumber: String, isValid: Boolean) {
        when {
            firstName.isEmpty() && phoneNumber.isEmpty() -> updateState {
                ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameEmptyAndNumberEmpty)
            }
            firstName.isEmpty() && !isValid -> updateState {
                ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameEmptyAndNumberIncorrect)
            }
            firstName.isEmpty() && isValid -> updateState {
                ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameEmptyAndNumberCorrect)
            }
            firstName.isNotEmpty() && phoneNumber.isEmpty() -> updateState {
                ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameFilledAndNumberEmpty)
            }
            firstName.isNotEmpty() && !isValid -> updateState {
                ContactAddState.ContactAddStatus(ContactAddFragment.EditTextStatus.NameFilledAndNumberIncorrect)
            }
            else -> {
                contactsLoaderScope.launch {
                    contactsLoader.insertContact(firstName, secondName, phoneNumber) {
                        exitWithResult(ContactAddNavigationContract.createResult(this))
                    }
                }
            }
        }
    }
}