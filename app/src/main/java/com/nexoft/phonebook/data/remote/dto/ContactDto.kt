package com.nexoft.phonebook.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ContactDto(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("surname")
    val surname: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("image")
    val image: String? = null
)