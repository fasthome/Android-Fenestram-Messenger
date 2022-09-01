package io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsRequest
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsResponse
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import java.text.Collator
import java.time.ZonedDateTime
import java.util.*
import java.util.Collections.sort

class ContactsMapper(private val profileImageUrlConverter: ProfileImageUrlConverter) {

    fun contactListToRequest(contacts: List<Contact>): List<ContactsRequest> =
        contacts.mapNotNull {
            contactToRequest(it)
        }


    fun contactToRequest(contact: Contact): ContactsRequest? {
        val phone = contact.phone
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .trim()

        if (phone.length < 9
            || phone.contains('*')
            || phone.contains('#')){
            return null
        }

        var subphone = phone
        if (phone.length >= 10) {
            subphone = "+7" + phone.substring(phone.length - 10, phone.length)
        }

        return ContactsRequest(
            name = contact.userName,
            phone = subphone
        )
    }

    fun responseToContactsList(contactsResponse: List<ContactsResponse>): List<Contact> {
        val contacts = contactsResponse.map {
            val user = it.user
            Contact(
                id = it.id,
                phone = it.phone,
                userName = it.name,
                userId = it.user?.id,
                user = user?.let {
                    User(
                        id = user.id,
                        phone = user.phone,
                        name = user.name ?: "",
                        nickname = user.nickname ?: "",
                        email = user.email ?: "",
                        birth = user.birth ?: "",
                        avatar = profileImageUrlConverter.convert(user.avatar),
                        isOnline = user.isOnline,
                        lastActive = ZonedDateTime.now()
                    )
                }
            )
        }

        val locale = Locale("ru", "RU")
        val collator: Collator = Collator.getInstance(locale)

        sort(contacts) { o1: Contact, o2: Contact ->
            collator.compare(o1.userName, o2.userName)
        }

        return contacts.sortedWith(compareBy {
            it.user == null
        })
    }

}