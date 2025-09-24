package com.nexoft.phonebook.di

import android.content.Context
import androidx.room.Room
import com.nexoft.phonebook.data.local.dao.ContactDao
import com.nexoft.phonebook.data.local.dao.SearchHistoryDao
import com.nexoft.phonebook.data.local.database.PhoneBookDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePhoneBookDatabase(
        @ApplicationContext context: Context
    ): PhoneBookDatabase {
        return Room.databaseBuilder(
            context,
            PhoneBookDatabase::class.java,
            "phonebook_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideContactDao(database: PhoneBookDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(database: PhoneBookDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
}