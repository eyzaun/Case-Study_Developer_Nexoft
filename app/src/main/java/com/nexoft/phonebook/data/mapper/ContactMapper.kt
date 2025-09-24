package com.nexoft.phonebook.data.mapper

import com.nexoft.phonebook.data.local.entity.ContactEntity
import com.nexoft.phonebook.domain.model.Contact

fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        isInDeviceContacts = isInDeviceContacts
    )
}

fun ContactEntity.toDomainModel(): Contact {
    return Contact(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl,
        createdAt = createdAt,
        isInDeviceContacts = isInDeviceContacts
    )
}

fun List<ContactEntity>.toDomainModels(): List<Contact> = map { it.toDomainModel() }
fun List<Contact>.toEntities(): List<ContactEntity> = map { it.toEntity() }
