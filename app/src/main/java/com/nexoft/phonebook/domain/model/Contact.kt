package com.nexoft.phonebook.domain.model

data class Contact(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImageUrl: String?,
    val createdAt: String,
    val isInDeviceContacts: Boolean
)
package com.nexoft.phonebook.domain.model

data class Contact(
    val id: String = "",
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImageUrl: String? = null,
    val createdAt: String = "",
    val isInDeviceContacts: Boolean = false
) {
    val fullName: String
        get() = "$firstName $lastName"

    val initials: String
        get() = "${firstName.firstOrNull()?.uppercase() ?: ""}${lastName.firstOrNull()?.uppercase() ?: ""}"
}