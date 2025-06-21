package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class RegisterSuccessResponse(

	@field:SerializedName("data")
	val data: DescRegisterJSON? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DescRegisterJSON(

	@field:SerializedName("full_name")
	val fullName: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
