package com.nexoft.phonebook.utils

object ValidationHelper {

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("This field is required")
            name.length < 2 -> ValidationResult.Error("Must be at least 2 characters")
            name.length > 50 -> ValidationResult.Error("Must be 50 characters or fewer")
            !name.all { it.isLetter() || it.isWhitespace() } -> ValidationResult.Error("Letters only")
            else -> ValidationResult.Success
        }
    }

    fun validatePhoneNumber(phoneNumber: String): ValidationResult {
        val cleaned = phoneNumber.filter { it.isDigit() }
        return when {
            cleaned.isEmpty() -> ValidationResult.Error("Phone number is required")
            cleaned.length < 10 -> ValidationResult.Error("Please enter a valid phone number")
            cleaned.length > 11 -> ValidationResult.Error("Phone number is too long")
            else -> ValidationResult.Success
        }
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}