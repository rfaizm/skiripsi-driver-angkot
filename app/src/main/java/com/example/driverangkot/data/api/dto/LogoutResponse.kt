package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class LogoutResponse(
    @field:SerializedName("message")
    val message: String? = null
)