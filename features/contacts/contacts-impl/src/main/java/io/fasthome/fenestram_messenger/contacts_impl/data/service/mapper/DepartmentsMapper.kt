package io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.DepartmentModel
import io.fasthome.fenestram_messenger.contacts_api.model.DivisionModel
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsRequest
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.ContactsResponse
import io.fasthome.fenestram_messenger.contacts_impl.data.service.model.DepartmentResponse
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.DepartmentViewItem
import io.fasthome.fenestram_messenger.data.StorageUrlConverter
import io.fasthome.fenestram_messenger.util.Country
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setMaskByCountry
import java.text.Collator
import java.time.ZonedDateTime
import java.util.*
import java.util.Collections.sort

class DepartmentsMapper(private val profileImageUrlConverter: StorageUrlConverter) {

    fun responseToDepartmentList(response: DepartmentResponse) = null

    fun departmentModelToViewItem(models: List<DepartmentModel>): List<DepartmentViewItem> {
        val viewItems = mutableListOf<DepartmentViewItem>()
        models.forEach { depModel ->
            viewItems.add(DepartmentViewItem.Header(PrintableText.Raw(depModel.title)))
            depModel.division.map {
                viewItems.add(it.toDivisionViewItem())
            }
        }
        return viewItems
    }

    private fun DivisionModel.toDivisionViewItem() = DepartmentViewItem.Division(
        title = PrintableText.Raw(title),
        employee = mapToContactViewItem(employee)
    )

    fun contactToViewItem(contact: Contact, selfUserPhone: String?): ContactsViewItem {
        return when {
            contact.user != null -> {
                val user = contact.user!!
                ContactsViewItem(
                    userId = user.id,
                    avatar = user.avatar,
                    name = getName(contact, selfUserPhone)
                )
            }
            else -> {
                error("Unknown type contact")
            }
        }
    }

    private fun getName(contact: Contact, selfUserPhone: String?): PrintableText {
        val user = contact.user!!

        val userName = when {
            contact.userName?.isNotEmpty() == true -> contact.userName!!
            user.name.isNotEmpty() -> user.name
            else -> user.phone.setMaskByCountry(Country.RUSSIA)
        }

        return if (contact.phone == selfUserPhone || user.phone == selfUserPhone)
            PrintableText.StringResource(R.string.self_contact_name, userName)
        else PrintableText.Raw(userName)
    }


    fun mapToContactViewItem(employees: List<Contact>) = employees.map { contact ->
        contactToViewItem(contact,null)
    }
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
                        contactName = null,
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