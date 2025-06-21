package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class UpdateLocationResponse(

	@field:SerializedName("angkot")
	val angkot: Angkot? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Angkot(

	@field:SerializedName("driver_id")
	val driverId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("max_capacity")
	val maxCapacity: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("plat_nomor")
	val platNomor: String? = null,

	@field:SerializedName("trayek_id")
	val trayekId: Int? = null,

	@field:SerializedName("lat")
	val lat: String? = null,

	@field:SerializedName("long")
	val jsonMemberLong: String? = null
)
