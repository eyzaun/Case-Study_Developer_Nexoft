package com.nexoft.phonebook.data.mapper

import com.nexoft.phonebook.data.remote.dto.UserResponse
import com.nexoft.phonebook.domain.model.Contact

fun UserResponse.toDomainModel(): Contact {
    return Contact(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        isInDeviceContacts = false
    )
}

fun List<UserResponse>.toDomainModels(): List<Contact> = map { it.toDomainModel() }
