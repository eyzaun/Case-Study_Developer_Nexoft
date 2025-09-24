package com.nexoft.phonebook.domain.usecase

import com.nexoft.phonebook.domain.repository.ContactRepository
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contactId: String): Result<Unit> {
        return repository.deleteContact(contactId)
    }
}