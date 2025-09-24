package com.nexoft.phonebook.domain.usecase

import com.nexoft.phonebook.domain.repository.ContactRepository
import javax.inject.Inject

class CheckDeviceContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke() {
        repository.syncWithDevice()
    }
}