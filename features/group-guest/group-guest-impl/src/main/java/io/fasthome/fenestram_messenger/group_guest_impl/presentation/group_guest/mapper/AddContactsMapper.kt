/**
 * Created by Dmitry Popov on 15.08.2022.
 */
package io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.mapper

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.group_guest_impl.presentation.group_guest.model.AddContactViewItem
import io.fasthome.fenestram_messenger.util.Country
import io.fasthome.fenestram_messenger.util.PrintableText
import io.fasthome.fenestram_messenger.util.setMaskByCountry

fun mapToContactViewItem(contact: Contact) =
    AddContactViewItem.AddContact(
        userId = contact.userId,
        userName = PrintableText.Raw(getName(contact))
    ) as AddContactViewItem

private fun getName(contact: Contact) : String{
    val user = contact.user!!
    return when{
        contact.userName?.isNotEmpty() == true -> contact.userName!!
        user.name.isNotEmpty() -> user.name
        else -> user.phone.setMaskByCountry(Country.RUSSIA)
    }
}