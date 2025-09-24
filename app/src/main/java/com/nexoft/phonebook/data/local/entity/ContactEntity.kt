package com.nexoft.phonebook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImageUrl: String?,
    val createdAt: String,
    val isInDeviceContacts: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)