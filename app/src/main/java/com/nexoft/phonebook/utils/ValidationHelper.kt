package com.nexoft.phonebook.utils

object ValidationHelper {

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("Bu alan zorunludur")
            name.length < 2 -> ValidationResult.Error("En az 2 karakter olmalıdır")
            name.length > 50 -> ValidationResult.Error("En fazla 50 karakter olabilir")
            !name.all { it.isLetter() || it.isWhitespace() } -> ValidationResult.Error("Sadece harf içermelidir")
            else -> ValidationResult.Success
        }
    }

    fun validatePhoneNumber(phoneNumber: String): ValidationResult {
        val cleaned = phoneNumber.filter { it.isDigit() }
        return when {
            cleaned.isEmpty() -> ValidationResult.Error("Telefon numarası zorunludur")
            cleaned.length < 10 -> ValidationResult.Error("Geçerli bir telefon numarası giriniz")
            cleaned.length > 11 -> ValidationResult.Error("Telefon numarası çok uzun")
            else -> ValidationResult.Success
        }
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}