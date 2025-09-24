package com.nexoft.phonebook.data.repository

import com.nexoft.phonebook.data.local.dao.ContactDao
import com.nexoft.phonebook.data.local.dao.SearchHistoryDao
import com.nexoft.phonebook.data.local.entity.SearchHistoryEntity
import com.nexoft.phonebook.data.mapper.*
import com.nexoft.phonebook.data.remote.api.ContactsApi
import com.nexoft.phonebook.data.remote.dto.CreateUserRequest
import com.nexoft.phonebook.data.remote.dto.UpdateUserRequest
import com.nexoft.phonebook.domain.model.Contact
import com.nexoft.phonebook.domain.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val api: ContactsApi,
    private val contactDao: ContactDao,
    private val searchHistoryDao: SearchHistoryDao
) : ContactRepository {
    
    override suspend fun getAllContacts(): Result<List<Contact>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllContacts()
            if (response.isSuccessful && response.body()?.success == true) {
                val contacts = response.body()?.data?.users?.toDomainModels() ?: emptyList()
                
                // Cache to local database
                contactDao.deleteAllContacts()
                contactDao.insertContacts(contacts.toEntities())
                
                Result.success(contacts)
            } else {
                // Fallback to local data
                val localContacts = contactDao.getAllContacts().toDomainModels()
                if (localContacts.isNotEmpty()) {
                    Result.success(localContacts)
                } else {
                    Result.failure(Exception(response.body()?.messages?.firstOrNull() ?: "Failed to get contacts"))
                }
            }
        } catch (e: Exception) {
            // Fallback to local data on network error
            val localContacts = contactDao.getAllContacts().toDomainModels()
            if (localContacts.isNotEmpty()) {
                Result.success(localContacts)
            } else {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun getContact(id: String): Result<Contact> = withContext(Dispatchers.IO) {
        try {
            val response = api.getContact(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val contact = response.body()?.data?.toDomainModel()
                contact?.let {
                    contactDao.insertContact(it.toEntity())
                    Result.success(it)
                } ?: Result.failure(Exception("Contact not found"))
            } else {
                // Fallback to local data
                val localContact = contactDao.getContact(id)?.toDomainModel()
                localContact?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Contact not found"))
            }
        } catch (e: Exception) {
            // Fallback to local data
            val localContact = contactDao.getContact(id)?.toDomainModel()
            localContact?.let {
                Result.success(it)
            } ?: Result.failure(e)
        }
    }
    
    override suspend fun addContact(contact: Contact): Result<Contact> = withContext(Dispatchers.IO) {
        try {
            val request = CreateUserRequest(
                firstName = contact.firstName,
                lastName = contact.lastName,
                phoneNumber = contact.phoneNumber,
                profileImageUrl = contact.profileImageUrl
            )
            
            val response = api.addContact(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val newContact = response.body()?.data?.toDomainModel()
                newContact?.let {
                    contactDao.insertContact(it.toEntity())
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to add contact"))
            } else {
                Result.failure(Exception(response.body()?.messages?.firstOrNull() ?: "Failed to add contact"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateContact(id: String, contact: Contact): Result<Contact> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateUserRequest(
                firstName = contact.firstName,
                lastName = contact.lastName,
                phoneNumber = contact.phoneNumber,
                profileImageUrl = contact.profileImageUrl
            )
            
            val response = api.updateContact(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                val updatedContact = response.body()?.data?.toDomainModel()
                updatedContact?.let {
                    contactDao.updateContact(it.toEntity())
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to update contact"))
            } else {
                Result.failure(Exception(response.body()?.messages?.firstOrNull() ?: "Failed to update contact"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteContact(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteContact(id)
            if (response.isSuccessful && response.body()?.success == true) {
                contactDao.deleteContact(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.messages?.firstOrNull() ?: "Failed to delete contact"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun uploadImage(imageFile: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            
            val response = api.uploadImage(body)
            if (response.isSuccessful && response.body()?.success == true) {
                val imageUrl = response.body()?.data?.imageUrl
                imageUrl?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Failed to upload image"))
            } else {
                Result.failure(Exception(response.body()?.messages?.firstOrNull() ?: "Failed to upload image"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun searchContacts(query: String): Flow<List<Contact>> {
        return if (query.isBlank()) {
            contactDao.getAllContactsFlow().map { it.toDomainModels() }
        } else {
            contactDao.searchContacts(query).map { it.toDomainModels() }
        }
    }
    
    override suspend fun getSearchHistory(): List<String> = withContext(Dispatchers.IO) {
        searchHistoryDao.getUniqueSearchQueries()
    }
    
    override suspend fun saveSearchQuery(query: String) = withContext(Dispatchers.IO) {
        if (query.isNotBlank()) {
            searchHistoryDao.deleteSearch(query) // Remove duplicate
            searchHistoryDao.insertSearch(SearchHistoryEntity(query = query))
        }
    }
    
    override suspend fun clearSearchHistory() = withContext(Dispatchers.IO) {
        searchHistoryDao.clearSearchHistory()
    }
    
    override suspend fun syncWithDevice() = withContext(Dispatchers.IO) {
        // This will be implemented in presentation layer with DeviceContactsHelper
        // Here we just update the database status
    }
}
