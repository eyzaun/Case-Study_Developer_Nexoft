package com.nexoft.phonebook.domain.usecase

import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.domain.repository.ContactRepository
import javax.inject.Inject

class GetAllContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(): Result<Map<Char, List<Contact>>> {
        return repository.getAllContacts().map { contacts ->
            contacts
                .sortedBy { it.fullName.uppercase() }
                .groupBy { contact ->
                    contact.fullName.firstOrNull()?.uppercaseChar() ?: '#'
                }
        }
    }
}