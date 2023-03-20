/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.domain.repo

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.DepartmentModel
import io.fasthome.fenestram_messenger.util.CallResult


interface DepartmentRepo {
    suspend fun getDepartments(): CallResult<List<DepartmentModel>>

    suspend fun uploadContacts(contacts: List<Contact>): CallResult<Unit>

    suspend fun loadContacts(): CallResult<List<Contact>>

}