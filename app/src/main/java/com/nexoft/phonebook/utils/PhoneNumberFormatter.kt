package com.nexoft.phonebook.utils

object PhoneNumberFormatter {

    fun formatPhoneNumber(phoneNumber: String): String {
        val cleaned = phoneNumber.filter { it.isDigit() }

        return when {
            cleaned.length == 11 && cleaned.startsWith("0") -> {
                // Format: 0 5XX XXX XX XX
                buildString {
                    append(cleaned.substring(0, 1))
                    append(" ")
                    append(cleaned.substring(1, 4))
                    append(" ")
                    append(cleaned.substring(4, 7))
                    append(" ")
                    append(cleaned.substring(7, 9))
                    append(" ")
                    append(cleaned.substring(9, 11))
                }
            }
            cleaned.length == 10 && cleaned.startsWith("5") -> {
                // Format: 5XX XXX XX XX
                buildString {
                    append(cleaned.substring(0, 3))
                    append(" ")
                    append(cleaned.substring(3, 6))
                    append(" ")
                    append(cleaned.substring(6, 8))
                    append(" ")
                    append(cleaned.substring(8, 10))
                }
            }
            else -> phoneNumber
        }
    }

    fun normalizePhoneNumber(phoneNumber: String): String {
        return phoneNumber
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace("+", "")
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val cleaned = phoneNumber.filter { it.isDigit() }
        return cleaned.length >= 10
    }
}