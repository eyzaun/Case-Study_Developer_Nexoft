package com.nexoft.phonebook.data.remote.api

import com.nexoft.phonebook.data.remote.dto.ApiResponse
import com.nexoft.phonebook.data.remote.dto.CreateUserRequest
import com.nexoft.phonebook.data.remote.dto.EmptyResponse
import com.nexoft.phonebook.data.remote.dto.UpdateUserRequest
import com.nexoft.phonebook.data.remote.dto.UploadImageResponse
import com.nexoft.phonebook.data.remote.dto.UserListResponse
import com.nexoft.phonebook.data.remote.dto.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ContactsApi {

    @GET("api/User/GetAll")
    suspend fun getAllContacts(): Response<ApiResponse<UserListResponse>>

    @GET("api/User/{id}")
    suspend fun getContact(@Path("id") id: String): Response<ApiResponse<UserResponse>>

    @POST("api/User")
    suspend fun addContact(@Body request: CreateUserRequest): Response<ApiResponse<UserResponse>>

    @PUT("api/User/{id}")
    suspend fun updateContact(
        @Path("id") id: String,
        @Body request: UpdateUserRequest
    ): Response<ApiResponse<UserResponse>>

    @DELETE("api/User/{id}")
    suspend fun deleteContact(@Path("id") id: String): Response<ApiResponse<EmptyResponse>>

    @Multipart
    @POST("api/User/UploadImage")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<ApiResponse<UploadImageResponse>>
}