package com.nexoft.phonebook.domain.usecase

import com.nexoft.phonebook.domain.repository.ContactRepository
import java.io.File
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(imageFile: File): Result<String> {
        return repository.uploadImage(imageFile)
    }
}