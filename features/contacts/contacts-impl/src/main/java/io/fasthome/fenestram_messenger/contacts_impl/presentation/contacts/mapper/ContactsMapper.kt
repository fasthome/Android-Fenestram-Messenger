package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.util.Country
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setMaskByCountry

object ContactsMapper {
    fun contactsListToViewList(
        contacts: List<Contact>,
        selfUserPhone: String?
    ): List<ContactsViewItem> {
        val viewItems = contacts.map {
            contactToViewItem(it, selfUserPhone)
        }.toMutableList()

        val apiContactsSize = viewItems.filterIsInstance<ContactsViewItem.Api>().size

        viewItems.add(
            apiContactsSize, ContactsViewItem.Header(
                avatar = Any(),
                name = PrintableText.StringResource(R.string.contacts_unregister_users)
            )
        )

        return viewItems
    }

    fun contactToViewItem(contact: Contact, selfUserPhone: String?): ContactsViewItem {
        return when {
            contact.user == null -> {
                ContactsViewItem.Local(
                    avatar = R.drawable.ic_avatar_placeholder,
                    name = PrintableText.Raw(contact.userName ?: contact.phone)
                )
            }
            contact.user != null -> {
                val user = contact.user!!
                ContactsViewItem.Api(
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

}