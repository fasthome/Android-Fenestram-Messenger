/**
 * Created by Dmitry Popov on 16.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.domain.logic

import android.Manifest
import androidx.annotation.RequiresPermission
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.DepartmentModel
import io.fasthome.fenestram_messenger.contacts_impl.data.ContactsLoader
import io.fasthome.fenestram_messenger.contacts_impl.domain.repo.DepartmentRepo
import io.fasthome.fenestram_messenger.data.UserStorage
import io.fasthome.fenestram_messenger.util.CallResult
import io.fasthome.fenestram_messenger.util.getOrThrow


class DepartmentInteractor(
    private val departmentRepo: DepartmentRepo,
    private val userStorage: UserStorage,
    private val contactsLoader: ContactsLoader,
) {

    /***
     * При каждом получении контактов отправляются локальные контакты с устройства для актуализации,
     * успешна ли была отправка игнорируем, т.к. основная цель функции выгрузить уже существующие контакты
     */
    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    suspend fun getContactsAndUploadContacts(): CallResult<List<Contact>> {
        val localContacts: List<Contact> = contactsLoader.fetchLocalContacts().map {
            Contact(
                id = 0,
                phone = it.phone,
                userName = it.name,
                userId = null
            )
        }

        departmentRepo.uploadContacts(localContacts)

        return departmentRepo.loadContacts()
    }

    /**
     * Запрос на получение всех департаментов компании.
     */
    suspend fun getDepartments(): CallResult<List<DepartmentModel>> =
        departmentRepo.getDepartments()


}