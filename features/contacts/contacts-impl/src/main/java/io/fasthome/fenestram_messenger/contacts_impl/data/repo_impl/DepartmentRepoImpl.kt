/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.data.repo_impl

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.DepartmentModel
import io.fasthome.fenestram_messenger.contacts_impl.data.service.DepartmentService
import io.fasthome.fenestram_messenger.contacts_impl.domain.repo.DepartmentRepo
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.callForResult


class DepartmentRepoImpl(private val departmentService: DepartmentService,) :
    DepartmentRepo {

    override suspend fun getDepartments(): CallResult<List<DepartmentModel>> = callForResult {
        departmentService.getDepartments()
    }

    override suspend fun uploadContacts(contacts: List<Contact>): CallResult<Unit> = callForResult {
        departmentService.sendContacts(contacts)
    }

    override suspend fun loadContacts(): CallResult<List<Contact>> = callForResult {
        departmentService.getContacts()
    }

}