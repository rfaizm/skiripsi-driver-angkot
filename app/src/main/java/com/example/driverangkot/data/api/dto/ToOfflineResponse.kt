package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class ToOfflineResponse(

	@field:SerializedName("data")
	val data: Status? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Status(

	@field:SerializedName("status_online")
	val statusOnline: Boolean? = null
)
