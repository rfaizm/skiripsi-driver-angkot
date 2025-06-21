package com.example.driverangkot.data.api.dto

import com.google.gson.annotations.SerializedName

data class LoginSuccessResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: UserJSON? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class DriverJSON(

	@field:SerializedName("driver_id")
	val driverId: Int? = null,

	@field:SerializedName("no_hp")
	val noHp: String? = null,

	@field:SerializedName("no_hp_emergency")
	val noHpEmergency: String? = null,

	@field:SerializedName("plat_nomor")
	val platNomor: String? = null,

	@field:SerializedName("trayek_id")
	val trayekId: Int? = null
)

data class UserJSON(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("driver")
	val driver: DriverJSON? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
