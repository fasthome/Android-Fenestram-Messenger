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
            isNameFilled = false,
            isNumberEmpty = false,
            isNumberCorrect = false,
            idle = true
        )
    }

    fun navigateBack() {
        router.exit()
    }

    fun onNameChanged(name: String) {
        if (name.isNotEmpty())
            updateState {
                ContactAddState(
                    true,
                    currentViewState.isNumberEmpty,
                    currentViewState.isNumberCorrect,
                    false
                )
            }
        else
            updateState {
                ContactAddState(
                    false,
                    currentViewState.isNumberEmpty,
                    currentViewState.isNumberCorrect,
                    false
                )
            }
    }

    fun onNumberChanged(number: String) {
        var isNumberCorrect = false
        if (number.length == 10) {
            isNumberCorrect = true
        }

        if (number.isNotEmpty())
            updateState {
                ContactAddState(
                    currentViewState.isNameFilled,
                    false,
                    isNumberCorrect,
                    false
                )
            }
        else
            updateState {
                ContactAddState(
                    currentViewState.isNameFilled,
                    true,
                    currentViewState.isNumberCorrect,
                    false
                )
            }
    }

    fun writeContact(firstName: String, secondName: String, phoneNumber: String) {
        contactsLoader.onStartWriting(ContactWriteItem(firstName, secondName, phoneNumber))
    }
}