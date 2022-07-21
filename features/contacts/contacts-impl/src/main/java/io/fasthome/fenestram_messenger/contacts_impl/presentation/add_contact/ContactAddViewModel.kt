package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.model.ContactAddResult
import io.fasthome.fenestram_messenger.contacts_impl.presentation.util.ContactsLoader
import io.fasthome.fenestram_messenger.mvi.BaseViewModel
import io.fasthome.fenestram_messenger.navigation.ContractRouter
import io.fasthome.fenestram_messenger.navigation.model.RequestParams
import kotlinx.coroutines.launch

class ContactAddViewModel(
    router: ContractRouter,
    requestParams: RequestParams,
    private val permissionInterface: PermissionInterface,
    private val contactsLoader: ContactsLoader
) : BaseViewModel<ContactAddState, ContactAddEvent>(router, requestParams) {

    init {
        requestPermissionToWriteContacts()
    }

    private fun requestPermissionToWriteContacts() {
        viewModelScope.launch {
            val permissionGranted = permissionInterface.request(Manifest.permission.WRITE_CONTACTS)

            if (!permissionGranted) {
                router.exit()
            }

        }
    }

    override fun createInitialState(): ContactAddState {
        return ContactAddState(ContactAddFragment.EditTextStatus.NameIdle)
    }

    fun navigateBack() {
        router.exit()
    }

    fun onNameTextChanged(firstName: String, phoneNumber: String) {
        if (firstName.isNotEmpty() && phoneNumber.length == 10) {
            updateState { ContactAddState(ContactAddFragment.EditTextStatus.NameFilledAndNumberCorrect) }
        } else {
            updateState { ContactAddState(ContactAddFragment.EditTextStatus.NameIdle) }
        }
    }

    fun onNumberTextChanged(firstName: String, phoneNumber: String) {
        if (firstName.isNotEmpty() && phoneNumber.length == 10) {
            updateState { ContactAddState(ContactAddFragment.EditTextStatus.NameFilledAndNumberCorrect) }
        } else {
            updateState { ContactAddState(ContactAddFragment.EditTextStatus.NumberIdle) }
        }
    }

    fun checkAndWriteContact(firstName: String, secondName: String, phoneNumber: String) {
        when {
            firstName.isEmpty() && phoneNumber.isEmpty() -> updateState {
                ContactAddState(ContactAddFragment.EditTextStatus.NameEmptyAndNumberEmpty)
            }
            firstName.isEmpty() && phoneNumber.length != 10 -> updateState {
                ContactAddState(ContactAddFragment.EditTextStatus.NameEmptyAndNumberIncorrect)
            }
            firstName.isEmpty() && phoneNumber.length == 10 -> updateState {
                ContactAddState(ContactAddFragment.EditTextStatus.NameEmptyAndNumberCorrect)
            }
            firstName.isNotEmpty() && phoneNumber.isEmpty() -> updateState {
                ContactAddState(ContactAddFragment.EditTextStatus.NameFilledAndNumberEmpty)
            }
            firstName.isNotEmpty() && phoneNumber.length != 10 -> updateState {
                ContactAddState(ContactAddFragment.EditTextStatus.NameFilledAndNumberIncorrect)
            }
            else -> {
                contactsLoader.insertContact(firstName, secondName, phoneNumber) {
                    exitWithResult(ContactAddNavigationContract.createResult(this))
                }
            }
        }
    }
}