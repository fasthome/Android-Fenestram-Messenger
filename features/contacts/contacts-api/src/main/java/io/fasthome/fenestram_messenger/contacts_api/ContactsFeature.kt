/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_api

import android.os.Parcelable
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.DepartmentModel
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult
import kotlinx.parcelize.Parcelize

interface ContactsFeature {

    val contactsNavigationContract: NavigationContractApi<NoParams, NoResult>
    val contactAddNavigationContract: NavigationContractApi<Params, ContactAddResult>

    suspend fun getDepartments(): CallResult<List<DepartmentModel>>
    suspend fun getContactsAndUploadContacts(): CallResult<List<Contact>>

    @Parcelize
    data class Params(
        val name: String? = null,
        val phone: String? = null
    ) : Parcelable

    sealed class ContactAddResult : Parcelable {

        @Parcelize
        object Success : ContactAddResult()

        @Parcelize
        object Canceled : ContactAddResult()
    }
}