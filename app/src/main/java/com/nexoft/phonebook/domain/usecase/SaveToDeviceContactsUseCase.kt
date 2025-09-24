package com.nexoft.phonebook.domain.usecase

import com.nexoft.phonebook.domain.model.Contact
import javax.inject.Inject

class SaveToDeviceContactsUseCase @Inject constructor() {
    suspend operator fun invoke(contact: Contact): Result<Unit> {
        // Bu use case presentation layer'da DeviceContactsHelper ile implement edilecek
        // Domain layer'da sadece interface tanımlaması yaptım
        return Result.success(Unit)
    }
}