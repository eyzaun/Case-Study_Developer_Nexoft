package com.nexoft.phonebook.utils

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import com.nexoft.phonebook.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DeviceContactsHelper {

    suspend fun saveContactToDevice(
        context: Context,
        contact: Contact
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val ops = ArrayList<ContentProviderOperation>()
            val rawContactInsertIndex = ops.size

            // Insert raw contact
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            // Insert name
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.GIVEN_NAME, contact.firstName)
                    .withValue(StructuredName.FAMILY_NAME, contact.lastName)
                    .build()
            )

            // Insert phone number
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, contact.phoneNumber)
                    .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                    .build()
            )

            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkContactInDevice(
        context: Context,
        phoneNumber: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = Phone.CONTENT_URI
            val projection = arrayOf(Phone.NUMBER)
            val selection = "${Phone.NUMBER} = ?"
            val selectionArgs = arrayOf(phoneNumber.replace(" ", "").replace("-", ""))

            context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                return@withContext cursor.count > 0
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllDeviceContactPhoneNumbers(
        context: Context
    ): Set<String> = withContext(Dispatchers.IO) {
        val phoneNumbers = mutableSetOf<String>()
        try {
            val uri = Phone.CONTENT_URI
            val projection = arrayOf(Phone.NUMBER)

            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val phoneIndex = cursor.getColumnIndex(Phone.NUMBER)
                while (cursor.moveToNext()) {
                    val phoneNumber = cursor.getString(phoneIndex)
                    if (!phoneNumber.isNullOrBlank()) {
                        phoneNumbers.add(normalizePhoneNumber(phoneNumber))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        phoneNumbers
    }

    private fun normalizePhoneNumber(phoneNumber: String): String {
        return phoneNumber
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace("+90", "0")
            .replace("+", "")
    }
}