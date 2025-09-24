package com.nexoft.phonebook.data.local.dao

import androidx.room.*
import com.nexoft.phonebook.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY firstName ASC, lastName ASC")
    suspend fun getAllContacts(): List<ContactEntity>
    
    @Query("SELECT * FROM contacts ORDER BY firstName ASC, lastName ASC")
    fun getAllContactsFlow(): Flow<List<ContactEntity>>
    
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContact(id: String): ContactEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)
    
    @Update
    suspend fun updateContact(contact: ContactEntity)
    
    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContact(id: String)
    
    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()
    
    @Query("""
        SELECT * FROM contacts 
        WHERE LOWER(firstName || ' ' || lastName) LIKE '%' || LOWER(:query) || '%' 
        OR LOWER(phoneNumber) LIKE '%' || LOWER(:query) || '%'
        ORDER BY firstName ASC, lastName ASC
    """)
    fun searchContacts(query: String): Flow<List<ContactEntity>>
    
    @Query("UPDATE contacts SET isInDeviceContacts = :isInDevice WHERE phoneNumber = :phoneNumber")
    suspend fun updateDeviceContactStatus(phoneNumber: String, isInDevice: Boolean)
}
