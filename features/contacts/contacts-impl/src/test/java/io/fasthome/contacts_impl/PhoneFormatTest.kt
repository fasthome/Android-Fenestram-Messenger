/**
 * Created by Dmitry Popov on 30.08.2022.
 */
package io.fasthome.contacts_impl

import io.fasthome.fenestram_messenger.contacts_api.model.Contact
import io.fasthome.fenestram_messenger.contacts_impl.data.service.mapper.DepartmentsMapper
import kotlin.test.assertEquals

fun main(){

    val contacts = listOf(
        Contact(
            id = 0,
            phone = "9141231212",
            null, null, null
        ),
        Contact(
            id = 0,
            phone = "+79141231212",
            null, null, null
        ),
        Contact(
            id = 0,
            phone = "89141231212",
            null, null, null
        ),
        Contact(
            id = 0,
            phone = "914(123)-12-12",
            null, null, null
        ),
        Contact(
            id = 0,
            phone = "8914(123)-12-12",
            null, null, null
        ),
    )
    contacts.forEach {
        val formatContact = DepartmentsMapper.contactToRequest(it)
        assertEquals(formatContact.phone.length, 12)
        assertEquals(formatContact.phone[0], '+')
    }

}