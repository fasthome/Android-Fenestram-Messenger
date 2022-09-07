/**
 * Created by Dmitry Popov on 16.08.2022.
 */
package io.fasthome.fenestram_messenger.contacts_impl.domain.logic

import android.Manifest
import androidx.annotation.RequiresPermission
import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.domain.repo.ContactsRepo
import io.fasthome.fenestram_messenger.contacts_impl.data.ContactsLoader
import io.fasthome.fenestram_messenger.contacts_impl.domain.entity.LocalContact
import io.fasthome.fenestram_messenger.util.*
import kotlinx.coroutines.delay

class ContactsInteractor(private val contactsRepo: ContactsRepo, private val contactsLoader: ContactsLoader) {

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

        contactsRepo.uploadContacts(localContacts)

        return contactsRepo.loadContacts()
    }

    /***
     * Если нам нужно просто получить контакты из ContactsFeature
     */
    suspend fun getContacts(): CallResult<List<Contact>> = contactsRepo.loadContacts()

    suspend fun deleteContacts(contactIds: List<Long>): CallResult<Unit> = contactsRepo.deleteContacts(contactIds)

    suspend fun deleteAllContacts(): CallResult<Unit> {
        val contactIds = getContacts()
            .getOrThrow()
            .map { it.id }
        return deleteContacts(contactIds)
    }

}