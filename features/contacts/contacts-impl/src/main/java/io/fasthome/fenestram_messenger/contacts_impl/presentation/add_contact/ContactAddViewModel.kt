package io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact

import android.Manifest
import androidx.lifecycle.viewModelScope
import io.fasthome.component.permission.PermissionInterface
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.model.ContactWriteItem
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

    private var _firstName: String = String()
    private var _phoneNumber: String = String()

    private fun requestPermissionToWriteContacts() {
        viewModelScope.launch {
            val permissionGranted = permissionInterface.request(Manifest.permission.WRITE_CONTACTS)

            if (!permissionGranted) {
                router.exit()
            }

        }
    }

    override fun createInitialState(): ContactAddState {
        return ContactAddState(
            isNameFilled = true,
            isNumberEmpty = false,
            isNumberCorrect = true,
            isButtonEnabled = false
        )
    }

    fun navigateBack() {
        router.exit()
    }

    fun onNameChanged(firstName: String) {
        _firstName = firstName
        val isButtonEnabled = _firstName.isNotEmpty() && _phoneNumber.length == 10
        if (!currentViewState.isNameFilled)
            updateState {
                ContactAddState(
                    true,
                    currentViewState.isNumberEmpty,
                    currentViewState.isNumberCorrect,
                    isButtonEnabled
                )
            }
        else {
            updateState {
                ContactAddState(
                    currentViewState.isNameFilled,
                    currentViewState.isNumberEmpty,
                    currentViewState.isNumberCorrect,
                    isButtonEnabled
                )
            }
        }
    }

    fun onNumberChanged(phoneNumber: String) {
        _phoneNumber = phoneNumber
        val isButtonEnabled = _firstName.isNotEmpty() && _phoneNumber.length == 10
        if (currentViewState.isNumberEmpty || !currentViewState.isNumberCorrect)
            updateState {
                ContactAddState(
                    currentViewState.isNameFilled,
                    isNumberEmpty = false,
                    isNumberCorrect = true,
                    isButtonEnabled
                )
            }
        else {
            updateState {
                ContactAddState(
                    currentViewState.isNameFilled,
                    currentViewState.isNumberEmpty,
                    currentViewState.isNumberCorrect,
                    isButtonEnabled
                )
            }
        }
    }

    fun checkAndWriteContact(firstName: String, secondName: String, phoneNumber: String) {
        when {
            firstName.isEmpty() && phoneNumber.isEmpty() -> updateState {
                ContactAddState(
                    isNameFilled = false,
                    isNumberEmpty = true,
                    isNumberCorrect = true,
                    isButtonEnabled = false
                )
            }
            firstName.isEmpty() && phoneNumber.length != 10 -> updateState {
                ContactAddState(
                    isNameFilled = false,
                    isNumberEmpty = false,
                    isNumberCorrect = false,
                    isButtonEnabled = false
                )
            }
            firstName.isEmpty() && phoneNumber.length == 10 -> updateState {
                ContactAddState(
                    isNameFilled = false,
                    isNumberEmpty = false,
                    isNumberCorrect = true,
                    isButtonEnabled = false
                )
            }
            firstName.isNotEmpty() && phoneNumber.isEmpty() -> updateState {
                ContactAddState(
                    isNameFilled = true,
                    isNumberEmpty = true,
                    isNumberCorrect = false,
                    isButtonEnabled = false
                )
            }
            firstName.isNotEmpty() && phoneNumber.length != 10 -> updateState {
                ContactAddState(
                    isNameFilled = true,
                    isNumberEmpty = false,
                    isNumberCorrect = false,
                    isButtonEnabled = false
                )
            }
            else -> {
                contactsLoader.insertContact("$firstName $secondName", phoneNumber)
                router.exit()
            }
        }
    }
}