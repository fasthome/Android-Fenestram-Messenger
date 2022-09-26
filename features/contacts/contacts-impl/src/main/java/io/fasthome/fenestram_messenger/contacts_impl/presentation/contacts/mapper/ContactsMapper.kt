package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_api.model.User
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem
import io.fasthome.fenestram_messenger.data.ProfileImageUrlConverter
import io.fasthome.fenestram_messenger.util.*

object ContactsMapper{
    fun contactsListToViewList(contacts: List<Contact>) : List<ContactsViewItem>{
        val viewItems = contacts.map {
            contactToViewItem(it)
        }.toMutableList()

        val apiContactsSize = viewItems.filterIsInstance<ContactsViewItem.Api>().size

        viewItems.add(apiContactsSize, ContactsViewItem.Header(
            avatar = Any(),
            name = PrintableText.StringResource(R.string.contacts_unregister_users)
        ))

        return viewItems
    }

    fun contactToViewItem(contact: Contact): ContactsViewItem {
        return when {
            contact.user == null -> {
                ContactsViewItem.Local(
                    avatar = R.drawable.ic_baseline_account_circle_24,
                    name = PrintableText.Raw(contact.userName ?: contact.phone)
                )
            }
            contact.user != null -> {
                val user = contact.user!!
                ContactsViewItem.Api(
                    userId = user.id,
                    avatar = user.avatar,
                    name = PrintableText.Raw(getName(contact))
                )
            }
            else -> {
                error("Unknown type contact")
            }
        }
    }

    private fun getName(contact: Contact) : String{
        val user = contact.user!!
        return when{
            contact.userName?.isNotEmpty() == true -> contact.userName!!
            user.name.isNotEmpty() -> user.name
            else -> user.phone.setMaskByCountry(Country.RUSSIA)
        }
    }

}