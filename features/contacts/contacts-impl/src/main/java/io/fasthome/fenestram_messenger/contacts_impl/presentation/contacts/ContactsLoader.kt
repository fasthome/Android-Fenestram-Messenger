package io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts

import android.content.Context
import android.provider.ContactsContract
import io.fasthome.fenestram_messenger.contacts_impl.presentation.contacts.model.ContactsViewItem


class ContactsLoader(private val context: Context) {

    private val _contactsList: MutableList<ContactsViewItem> = ArrayList()

    fun onStartLoading(): List<ContactsViewItem> {
        val cr = context.contentResolver

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            ContactsContract.Contacts.HAS_PHONE_NUMBER + ">0 AND LENGTH(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ")>0",
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (cursor != null && cursor.count > 0) {
            val mobileNoSet = HashSet<String>()
            cursor.use { cur ->
                while (cur.moveToNext()) {
                    val id =
                        cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                    val name =
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val number =
                        cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            .replace(" ", "")
                    if (!mobileNoSet.contains(number)) {
                        _contactsList.add(
                            ContactsViewItem(
                                id.toLong(),
                                0,
                                name,
                                8,
                            )
                        )
                        mobileNoSet.add(number)
                    }
                }
            }
        }
        return _contactsList.toList()
    }

}