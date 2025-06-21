package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class GetPlaceNameResponse(

	@field:SerializedName("data")
	val data: DataPlaceName? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataPlaceName(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("placeName")
	val placeName: String? = null,

	@field:SerializedName("lat")
	val lat: Double? = null,

	@field:SerializedName("long")
	val long: Double? = null
)
