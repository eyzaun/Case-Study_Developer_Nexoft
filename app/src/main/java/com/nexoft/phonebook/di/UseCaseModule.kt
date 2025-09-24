package com.nexoft.phonebook.di

import com.nexoft.phonebook.domain.repository.ContactRepository
import com.nexoft.phonebook.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideGetAllContactsUseCase(
        repository: ContactRepository
    ): GetAllContactsUseCase {
        return GetAllContactsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideAddContactUseCase(
        repository: ContactRepository
    ): AddContactUseCase {
        return AddContactUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateContactUseCase(
        repository: ContactRepository
    ): UpdateContactUseCase {
        return UpdateContactUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteContactUseCase(
        repository: ContactRepository
    ): DeleteContactUseCase {
        return DeleteContactUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchContactsUseCase(
        repository: ContactRepository
    ): SearchContactsUseCase {
        return SearchContactsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideUploadImageUseCase(
        repository: ContactRepository
    ): UploadImageUseCase {
        return UploadImageUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveToDeviceContactsUseCase(): SaveToDeviceContactsUseCase {
        return SaveToDeviceContactsUseCase()
    }

    @Provides
    @ViewModelScoped
    fun provideCheckDeviceContactsUseCase(
        repository: ContactRepository
    ): CheckDeviceContactsUseCase {
        return CheckDeviceContactsUseCase(repository)
    }
}