package com.nexoft.phonebook.data.remote.dto

import com.google.gson.annotations.SerializedName

// Generic API envelope based on swagger examples
// { success: bool, messages: [string], data: T, status: number }
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("messages") val messages: List<String>?,
    @SerializedName("data") val data: T?,
    @SerializedName("status") val status: Int
)

// Data shapes according to swagger

data class CreateUserRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)

data class UpdateUserRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)

data class UserListResponse(
    @SerializedName("users") val users: List<UserResponse>
)

data class UploadImageResponse(
    @SerializedName("imageUrl") val imageUrl: String
)

class EmptyResponse // represents {}
