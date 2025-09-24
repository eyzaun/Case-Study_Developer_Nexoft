package com.nexoft.phonebook.domain.repository

import com.nexoft.phonebook.domain.model.Contact
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ContactRepository {
    suspend fun getAllContacts(): Result<List<Contact>>
    suspend fun getContact(id: String): Result<Contact>
    suspend fun addContact(contact: Contact): Result<Contact>
    suspend fun updateContact(id: String, contact: Contact): Result<Contact>
    suspend fun deleteContact(id: String): Result<Unit>
    suspend fun uploadImage(imageFile: File): Result<String>
    fun searchContacts(query: String): Flow<List<Contact>>
    suspend fun getSearchHistory(): List<String>
    suspend fun saveSearchQuery(query: String)
    suspend fun clearSearchHistory()
    suspend fun syncWithDevice()
}
