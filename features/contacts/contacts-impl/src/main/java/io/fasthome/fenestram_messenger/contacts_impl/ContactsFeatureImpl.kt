/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl

import io.fasthome.fenestram_messenger.contacts_api.ContactsFeature
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.DepartmentModel
import io.fasthome.fenestram_messenger.contacts_impl.domain.logic.DepartmentInteractor
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.ContactAddNavigationContract
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.ContactsNavigationContract
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.contract.map
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult

class ContactsFeatureImpl(private val departmentInteractor: DepartmentInteractor) : ContactsFeature {
    override val contactsNavigationContract: NavigationContractApi<NoParams, NoResult> =
        ContactsNavigationContract
    override val contactAddNavigationContract: NavigationContractApi<ContactsFeature.Params, ContactsFeature.ContactAddResult> =
        ContactAddNavigationContract.map({
            ContactAddNavigationContract.Params(
                name = it.name,
                phone = it.phone
            )
        },
            resultMapper = {
                when (it) {
                    is ContactAddNavigationContract.ContactAddResult.Success -> ContactsFeature.ContactAddResult.Success
                    is ContactAddNavigationContract.ContactAddResult.Canceled -> ContactsFeature.ContactAddResult.Canceled
                }
            }
        )

    override suspend fun getDepartments(): CallResult<List<DepartmentModel>> = departmentInteractor.getDepartments()

    override suspend fun getContactsAndUploadContacts(): CallResult<List<Contact>> =
        departmentInteractor.getContactsAndUploadContacts()

}