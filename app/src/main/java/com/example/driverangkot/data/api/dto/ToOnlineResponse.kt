package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class ToOnlineResponse(

	@field:SerializedName("data")
	val data: AngkotDataJSON? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class AngkotData(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("long")
	val jsonMemberLong: String? = null
)

data class AngkotDataJSON(

	@field:SerializedName("status_online")
	val statusOnline: Boolean? = null,

	@field:SerializedName("angkot")
	val angkot: AngkotData? = null
)
