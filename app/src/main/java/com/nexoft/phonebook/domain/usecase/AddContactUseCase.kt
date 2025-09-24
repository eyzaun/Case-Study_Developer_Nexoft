package com.nexoft.phonebook.domain.usecase

import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.domain.repository.ContactRepository
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        profileImageUrl: String? = null
    ): Result<Contact> {
        val contact = Contact(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            phoneNumber = phoneNumber.trim(),
            profileImageUrl = profileImageUrl
        )
        return repository.addContact(contact)
    }
}