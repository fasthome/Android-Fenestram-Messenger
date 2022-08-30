/**
 * Created by Dmitry Popov on 04.07.2022.
 */
package io.fasthome.fenestram_messenger.contacts_api

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.navigation.contract.NavigationContractApi
import io.fasthome.fenestram_messenger.navigation.model.NoParams
import io.fasthome.fenestram_messenger.navigation.model.NoResult
import io.fasthome.fenestram_messenger.util.CallResult

interface ContactsFeature {

    val contactsNavigationContract : NavigationContractApi<NoParams, NoResult>

    suspend fun getContacts(): CallResult<List<Contact>>

    suspend fun deleteContacts(contactIds : List<Long>) : CallResult<Unit>

    suspend fun deleteAllContacts() : CallResult<Unit>
}