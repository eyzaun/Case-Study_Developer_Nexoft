package com.nexoft.phonebook.domain.usecase

import android.content.Context
import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.domain.repository.ContactRepository
import com.nexoft.phonebook.utils.DeviceContactsHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SaveToDeviceContactsUseCase @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contact: Contact): Result<Unit> {
        return try {
            DeviceContactsHelper.saveContactToDevice(appContext, contact)
            // After saving, resync device flags to persist isInDeviceContacts locally
            repository.syncWithDevice()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}