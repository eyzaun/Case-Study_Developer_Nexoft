package com.nexoft.phonebook.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nexoft.phonebook.data.local.dao.ContactDao
import com.nexoft.phonebook.data.local.dao.SearchHistoryDao
import com.nexoft.phonebook.data.local.entity.ContactEntity
import com.nexoft.phonebook.data.local.entity.SearchHistoryEntity

@Database(
    entities = [ContactEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PhoneBookDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
