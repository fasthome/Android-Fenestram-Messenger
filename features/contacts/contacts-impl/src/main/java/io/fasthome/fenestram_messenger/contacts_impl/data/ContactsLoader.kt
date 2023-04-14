package io.fasthome.fenestram_messenger.contacts_impl.data

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Context
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.RawContacts
import android.util.Log
import androidx.annotation.RequiresPermission
import io.fasthome.fenestram_messenger.contacts_impl.R
import io.fasthome.fenestram_messenger.contacts_impl.domain.entity.LocalContact
import io.fasthome.fenestram_messenger.contacts_impl.presentation.add_contact.ContactAddNavigationContract
import io.fasthome.fenestram_messenger.util.PrintableText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ContactsLoader(private val context: Context) {

    private var _contactsList: MutableList<LocalContact> = ArrayList()

    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    fun fetchLocalContacts(): List<LocalContact> {
        val cr = context.contentResolver
        _contactsList = ArrayList()

        val projection = arrayOf(
            CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            CommonDataKinds.Phone.NUMBER
        )

        val cursor = cr.query(
            CommonDataKinds.Phone.CONTENT_URI,
            projection,
            ContactsContract.Contacts.HAS_PHONE_NUMBER + ">0 AND LENGTH(" + CommonDataKinds.Phone.NUMBER + ")>0",
            null,
            CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (cursor != null && cursor.count > 0) {
            val mobileNoSet = HashSet<String>()
            cursor.use { cur ->
                while (cur.moveToNext()) {
                    val id =
                        cur.getString(cur.getColumnIndex(CommonDataKinds.Phone.CONTACT_ID))
                    val name =
                        cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val number =
                        cur.getString(cur.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                            .replace(" ", "")
                    if (!mobileNoSet.contains(number)) {
                        _contactsList.add(
                            LocalContact(
                                name = name,
                                phone = number
                            )
                        )
                        mobileNoSet.add(number)
                    }
                }
            }
        }
        return _contactsList.toList()
    }

    fun insertContact(
        firstName: String,
        secondName: String,
        mobileNumber: String,
        callback: ContactAddNavigationContract.ContactAddResult.() -> Unit
    ) {
        val ops = ArrayList<ContentProviderOperation>()
        val rawContactInsertIndex: Int = ops.size
        ops.add(
            ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null).build()
        )
        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(
                    CommonDataKinds.StructuredName.DISPLAY_NAME,
                    "$firstName $secondName"
                )
                .build()
        )
        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                    ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex
                )
                .withValue(ContactsContract.Data.MIMETYPE, CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(CommonDataKinds.Phone.NUMBER, "+${mobileNumber}")
                .withValue(CommonDataKinds.Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE).build()
        )

        try {
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            CoroutineScope(Dispatchers.Main).launch {
                callback(ContactAddNavigationContract.ContactAddResult.Success)
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                val message = PrintableText.StringResource(R.string.failed_to_save_contact)
                callback(ContactAddNavigationContract.ContactAddResult.Canceled(message))
            }
        }
    }

    @RequiresPermission(Manifest.permission.WRITE_CONTACTS)
    fun updateContactName(number: String, oldName: String, newName: String) {
        val cursor = context.contentResolver.query(
            CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(CommonDataKinds.Phone.CONTACT_ID, CommonDataKinds.Phone.NUMBER),
            CommonDataKinds.Phone.DISPLAY_NAME + "=?", arrayOf(oldName),
            null
        )

        if (cursor == null || cursor.count == 0) {
            insertContact(firstName = newName, secondName = "", mobileNumber = number, callback = {})
            return
        }

        var contactId: String? = null
        cursor.use { cur ->
            while (cur.moveToNext()) {
                val localNumber = cur.getString(cur.getColumnIndex(CommonDataKinds.Phone.NUMBER))
                if (localNumber.filter { it.isDigit() } == number) {
                    contactId = cur.getString(cur.getColumnIndex(CommonDataKinds.Phone.CONTACT_ID))
                    break
                }
            }
        }
        if (contactId == null) {
            insertContact(firstName = newName, secondName = "", mobileNumber = number, callback = {})
            return
        }

        val where = String.format(
            "%s = '%s' AND %s = ?",
            ContactsContract.Data.MIMETYPE,
            CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
            ContactsContract.Data.CONTACT_ID
        )
        val args = arrayOf(contactId)
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, args)
                .withValue(CommonDataKinds.StructuredName.MIDDLE_NAME, "")
                .build()
        )
        ops.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, args)
                .withValue(CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
                .build()
        )

        try {
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        } catch (e: Exception) {
            Log.d("ContactsLoader", e.message.toString())
        }
    }

}